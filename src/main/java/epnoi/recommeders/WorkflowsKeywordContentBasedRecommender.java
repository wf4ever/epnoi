package epnoi.recommeders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import epnoi.core.EpnoiCore;
import epnoi.model.Explanation;
import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.KeywordRecommenderParameters;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.RecommenderParameters;

public class WorkflowsKeywordContentBasedRecommender implements
		KeywordContentBasedRecommender {

	// static final int NUMBER_OF_QUERY_HITS = 10;

	Model model = null;
	Directory directory = null;
	IndexSearcher indexSearcher = null;
	QueryParser parser = null;
	ParametersModel parametersModel = null;

	private KeywordRecommenderParameters recommenderParameters;

	public WorkflowsKeywordContentBasedRecommender(
			RecommenderParameters initializationParameters,
			ParametersModel parametersModel) {
		this.recommenderParameters = (KeywordRecommenderParameters) initializationParameters;
		this.parametersModel = parametersModel;
	}

	// -------------------------------------------------------------------------------------------------

	public void recommend(RecommendationSpace recommedationSpace) {
		String queryExpression = null;
		ArrayList<String> queryTermsListAux = null;
		for (User user : this.model.getUsers()) {

			HashMap<String, Recommendation> recommendationsByItemURI = new HashMap<String, Recommendation>();
			if (user.getTagApplied().size() > 0) {
				/*
				 * System.out.println("User " + user.getName() + " tags " +
				 * _determineQueryTerms(user));
				 */
				for (ArrayList<String> queryTermsList : _determineQueryTerms(user)) {
					try {

						queryTermsListAux = queryTermsList;
						queryExpression = _buildQuery(queryTermsList);
						if (!queryExpression.equals("")) {
							Query query = parser.parse(queryExpression);

							TopDocs topHits = indexSearcher.search(query,
									this.recommenderParameters
											.getNumberOfQueryHits());
							/*
							 * System.out.println("(q:" + queryExpression +
							 * ") Recommendations for user " + user.getName() +
							 * " #> " + hits.totalHits);
							 */
							float maxScore = _scoreMax(topHits.scoreDocs);

							// Each of the top hits correspond to a
							// recommendation.
							for (ScoreDoc scoreDocument : topHits.scoreDocs) {

								// System.out.print(">" + scoreDocument.+" ");
								Document doc = indexSearcher
										.doc(scoreDocument.doc);
								// System.out.println(doc.get("filename"));

								String itemURI = doc.get("filename");
								if (!this.model.isWorkflow(itemURI)) {
									System.out
											.println("---------------------->no esta! "
													+ itemURI);
								}

								if (this.model.isWorkflow(itemURI)) {

									// there can be some discrepancies between
									// the model and the index!

									float normalizedScore = (scoreDocument.score / maxScore);
									float estimatedStrength = 0;
									int numberOfQueryTerms = queryTermsList
											.size();
									switch (numberOfQueryTerms) {
									case 3:
										estimatedStrength = normalizedScore * 5;
										break;
									case 2:
										estimatedStrength = normalizedScore * 3;
										break;
									case 1:
										estimatedStrength = normalizedScore * 1;
										break;
									}
									if (user.getWorkflows().contains(itemURI)) {
										/*
										 * System.out
										 * .println("The user was the owner of "
										 * + itemURI +
										 * "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
										 * );
										 */
									} else {// If the item is not part of the
											// user's
											// catalogue

										if (!recommendationsByItemURI
												.containsKey(itemURI)) {
											Recommendation newRecommendation = new Recommendation();
											newRecommendation
													.setRecommenderURI(this.recommenderParameters
															.getURI());

											newRecommendation
													.setItemURI(itemURI);

											Long itemID = null;

											Workflow workflow = this.model
													.getWorkflowByURI(itemURI);

											String explanationText = "The workflow entitled "
													+ workflow.getTitle()
													+ ("(URI:")
													+ workflow.getURI()
													+ ") is recommended to you since you used the following";

											if (numberOfQueryTerms == 1) {
												explanationText += " tag: ";
											} else {
												explanationText += " tags: ";
											}
											explanationText += (queryExpression
													.replace("contents:", ""))
													.replace("and", ",");

											if (numberOfQueryTerms == 1) {
												explanationText += "; and it partially describes its content";
											} else {
												explanationText += "; and they partially describes its content";
											}

											Explanation explanation = new Explanation();

											explanation
													.setExplanation(explanationText);
											explanation
													.setTimestamp(new Date(
															System.currentTimeMillis()));
											newRecommendation
													.setExplanation(explanation);
											if (workflow != null)
												itemID = workflow.getID();

											newRecommendation.setItemID(itemID);

											newRecommendation
													.setStrength(estimatedStrength);
											newRecommendation.setUserURI(user
													.getURI());

											Parameter parameterTechnique = new Parameter();
											parameterTechnique
													.setName(Provenance.TECHNIQUE);
											parameterTechnique
													.setValue(Provenance.TECHNIQUE_KEYWORD_CONTENT_BASED);
											Parameter parameter = new Parameter();

											parameter
													.setName(Provenance.ITEM_TYPE);
											parameter
													.setValue(Provenance.ITEM_TYPE_WORKFLOW);

											newRecommendation.getProvenance()
													.getParameters()
													.add(parameterTechnique);
											newRecommendation.getProvenance()
													.getParameters()
													.add(parameter);

											recommendationsByItemURI.put(
													itemURI, newRecommendation);

										} else {
											Recommendation recommendation = recommendationsByItemURI
													.get(itemURI);
											if (recommendation.getStrength() > estimatedStrength) {
												// The sugg
											} else {
												Workflow workflow = this.model
														.getWorkflowByURI(recommendation
																.getItemURI());

												String explanationText = "The workflow entitled "
														+ workflow.getTitle()
														+ ("(URI:")
														+ workflow.getURI()
														+ ") is recommended to you since you used the following";

												if (numberOfQueryTerms == 1) {
													explanationText += " tag: ";
												} else {
													explanationText += " tags: ";
												}
												explanationText += (queryExpression
														.replace("contents:",
																"")).replace(
														"and", ",");

												if (numberOfQueryTerms == 1) {
													explanationText += "; and it partially describes its content";
												} else {
													explanationText += "; and they partially describe its content";
												}
												recommendation
														.getExplanation()
														.setExplanation(
																explanationText);
												recommendation
														.getExplanation()
														.setTimestamp(
																new Date(
																		System.currentTimeMillis()));

												recommendation
														.setStrength(estimatedStrength);

											}
										}
									}

								}
							}
						}
					} catch (CorruptIndexException e) {
						// TODOto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {

						System.out.println("This is the expression "
								+ queryTermsList);
						System.out.println("This is the termlist "
								+ queryExpression);
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// Finally all the recommendations are added to the
				// recommendation
				// space
				// System.out.println(">>>>"+recommendationsByItemURI.values());
				for (Recommendation recommendation : recommendationsByItemURI
						.values()) {

					recommedationSpace.addRecommendationForUser(user,
							recommendation);
				}
			}

		}

	}

	// -------------------------------------------------------------------------------------------------

	private float _scoreAverage(ScoreDoc[] scoreDocs) {
		float average = 0;
		for (ScoreDoc scoreDoc : scoreDocs) {
			average += scoreDoc.score;

		}
		return average / scoreDocs.length;
	}

	// -------------------------------------------------------------------------------------------------

	private float _scoreMax(ScoreDoc[] scoreDocs) {
		float max = 0;
		for (ScoreDoc scoreDoc : scoreDocs) {
			if (scoreDoc.score > max)
				max = scoreDoc.score;

		}
		return max;
	}

	// -------------------------------------------------------------------------------------------------

	public void init(EpnoiCore epnoiCore) {
		this.model = epnoiCore.getModel();

		this.parser = null;
		try {
			String indexDirectory = this.recommenderParameters.getIndexPath();

			Directory dir = FSDirectory.open(new File(indexDirectory)); // 3
			this.indexSearcher = new IndexSearcher(dir);
			this.parser = new QueryParser(Version.LUCENE_30, // 4
					"contents", // 4
					new StandardAnalyzer( // 4
							Version.LUCENE_30));
			this.model = model;
		} catch (CorruptIndexException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	//-------------------------------------------------------------------------------------------------

	String _cleanSymbols(String tag) {
		tag = tag.toLowerCase();
		tag = tag.replaceAll("[^a-zA-Z 0-9]+", "");
		return tag;
	}

	// -------------------------------------------------------------------------------------------------

	private ArrayList<ArrayList<String>> _determineQueryTerms(User user) {

		ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();

		ArrayList<Tagging> orderedTags = _orderByFrequency(user.getTagApplied());
		if (orderedTags.size() == 1) {

			String queryExpression = "contents:"
					+ _cleanSymbols(orderedTags.get(0).getTag());
			ArrayList<String> queryExpressions = new ArrayList<String>();
			queryExpressions.add(queryExpression);
			queries.add(queryExpressions);
		} else if (orderedTags.size() == 2) {
			String queryExpressionA = "contents:"
					+ _cleanSymbols(orderedTags.get(0).getTag());
			String queryExpressionB = "contents:"
					+ _cleanSymbols(orderedTags.get(1).getTag());

			ArrayList<String> queryExpressionsA = new ArrayList<String>();
			ArrayList<String> queryExpressionsB = new ArrayList<String>();
			ArrayList<String> queryExpressionsIntersectionAB = new ArrayList<String>();
			queryExpressionsA.add(queryExpressionA);
			queryExpressionsB.add(queryExpressionB);
			queryExpressionsIntersectionAB.add(queryExpressionA);
			queryExpressionsIntersectionAB.add(queryExpressionB);
			queries.add(queryExpressionsA);
			queries.add(queryExpressionsB);
			queries.add(queryExpressionsIntersectionAB);

		} else if (orderedTags.size() >= 3) {
			String queryExpressionA = "contents:"
					+ _cleanSymbols(orderedTags.get(0).getTag());
			String queryExpressionB = "contents:"
					+ _cleanSymbols(orderedTags.get(1).getTag());
			String queryExpressionC = "contents:"
					+ _cleanSymbols(orderedTags.get(2).getTag());

			ArrayList<String> queryExpressionsA = new ArrayList<String>();
			ArrayList<String> queryExpressionsB = new ArrayList<String>();
			ArrayList<String> queryExpressionsC = new ArrayList<String>();

			ArrayList<String> queryExpressionsIntersectionAB = new ArrayList<String>();
			ArrayList<String> queryExpressionsIntersectionBC = new ArrayList<String>();
			ArrayList<String> queryExpressionsIntersectionAC = new ArrayList<String>();
			ArrayList<String> queryExpressionsIntersectionABC = new ArrayList<String>();

			queryExpressionsA.add(queryExpressionA);
			queryExpressionsB.add(queryExpressionB);
			queryExpressionsC.add(queryExpressionC);

			queryExpressionsIntersectionAB.add(queryExpressionA);
			queryExpressionsIntersectionAB.add(queryExpressionB);

			queryExpressionsIntersectionAC.add(queryExpressionA);
			queryExpressionsIntersectionAC.add(queryExpressionC);

			queryExpressionsIntersectionBC.add(queryExpressionB);
			queryExpressionsIntersectionBC.add(queryExpressionC);

			queryExpressionsIntersectionABC.add(queryExpressionA);
			queryExpressionsIntersectionABC.add(queryExpressionB);
			queryExpressionsIntersectionABC.add(queryExpressionC);

			queries.add(queryExpressionsA);
			queries.add(queryExpressionsB);
			queries.add(queryExpressionsC);
			queries.add(queryExpressionsIntersectionAB);
			queries.add(queryExpressionsIntersectionAC);
			queries.add(queryExpressionsIntersectionBC);
			queries.add(queryExpressionsIntersectionABC);

		}
		// System.out.println(">> "+queries);
		return queries;
	}

	// -------------------------------------------------------------------------------------------------

	public String _buildQuery(ArrayList<String> terms) {
		// System.out.println("Este es el que entra " + terms);
		String queryExpression = "";
		Iterator<String> termsIt = terms.iterator();
		if (termsIt.hasNext()) {
			String firstTerm = termsIt.next();
			if (!firstTerm.matches("contents:\\s*"))
				queryExpression = firstTerm;

			while (termsIt.hasNext()) {
				String next = termsIt.next();
				// if (next.length() < 12)
				// System.out.println("next........................." + next);
				if (!next.matches("contents:\\s*")) {
					queryExpression = queryExpression + " and " + next;
				}
			}
		}

		return queryExpression;
	}

	// -------------------------------------------------------------------------------------------------

	public RecommenderParameters getInitializationParameters() {
		return this.recommenderParameters;
	}

	// -------------------------------------------------------------------------------------------------

	public void close() {
		try {
			indexSearcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------

	private ArrayList<Tagging> _orderByFrequency(ArrayList<Tagging> taggingsList) {
		ArrayList<Tagging> taggingsListOrdered = (ArrayList<Tagging>) taggingsList
				.clone();
		Collections.sort(taggingsListOrdered);
		Collections.reverse(taggingsListOrdered);
		return taggingsListOrdered;

	}

	// -------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "WorkflowsKeywordContentBasedRecommender"
				+ this.getInitializationParameters().getURI();
	}
}
