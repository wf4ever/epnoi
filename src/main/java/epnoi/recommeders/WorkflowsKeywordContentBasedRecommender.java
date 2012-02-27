package epnoi.recommeders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

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

import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.Workflow;

public class WorkflowsKeywordContentBasedRecommender implements Recommender {

	//Recommedender parameters
	public static final String INDEX_PATH_PROPERTY = "INDEX_PATH";
	//Consider to make every parameter of the algorithm externally accessible
	
	static final int NUMBER_OF_QUERY_HITS = 10;


	Model model = null;
	Directory dir = null;
	IndexSearcher is = null;
	QueryParser parser = null;

	private Properties initializationProperties = null;

	public void recommend(RecommendationSpace recommedationSpace) {

		for (User user : this.model.getUsers()) {
			/*
			if (user.getTagApplied().size() > 0) {
				System.out.println("-----------------------------------------");
				System.out.println(" >>" + user.getName());
				System.out.println(" >>" + user.getURI());
				System.out.println("-----------------------------------------");
			}
			*/
			HashMap<String, Recommendation> recommendationsByItemURI = new HashMap<String, Recommendation>();
			if (user.getTagApplied().size() > 0) {
				
				for (ArrayList<String> queryTermsList : _determineQueryTerms(user)) {
					try {
						String queryExpression = _buildQuery(queryTermsList);

						Query query = parser.parse(queryExpression);

						TopDocs hits = is.search(query, NUMBER_OF_QUERY_HITS);
/*
						System.out.println("(q:" + queryExpression
								+ ") Recommendations for user "
								+ user.getName() + " #> " + hits.totalHits);
*/
						float maxScore = _scoreMax(hits.scoreDocs);
						for (ScoreDoc scoreDoc : hits.scoreDocs) {

							// System.out.print(">" + scoreDoc.score / maxScore+
							// " ");
							Document doc = is.doc(scoreDoc.doc);
							// System.out.println(doc.get("filename"));

							String itemURI = doc.get("filename");

							float normalizedScore = (scoreDoc.score / maxScore);
							float estimatedStrength = 0;
							int numberOfQueryTerms = queryTermsList.size();
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
								System.out
										.println("The user was the owner of "
												+ itemURI
												+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
												*/
							} else {

								if (!recommendationsByItemURI
										.containsKey(itemURI)) {
									Recommendation newRecommendation = new Recommendation();
									newRecommendation.setItemURI(itemURI);
									
									Long itemID = null;
									
									Workflow workflow = this.model.getWorkflowByURI(itemURI);
									if (workflow!=null)
										itemID = workflow.getID();
									
									newRecommendation.setItemID(itemID);

									newRecommendation
											.setStrength(estimatedStrength);
									newRecommendation.setUserURI(user.getURI());
									recommendationsByItemURI.put(itemURI,newRecommendation);
								/*
									System.out
											.println("The recommendation for "
													+ itemURI
													+ "is added with strenght of"
													+ estimatedStrength
													+ " and length"
													+ queryTermsList.size());
													*/
								} else {
									Recommendation recommendation = recommendationsByItemURI
											.get(itemURI);
									if (recommendation.getStrength() > estimatedStrength) {
										/*
										System.out
												.println("The strenght of the recommendation for "
														+ itemURI
														+ "was greater than "
														+ estimatedStrength);
														*/
									} else {
										/*
										System.out
												.println("The strenght is updated from "+recommendation.getStrength()+" to "
														+ estimatedStrength+ " for "+itemURI);
														*/

										recommendation
												.setStrength(estimatedStrength);
									}
								}
							}
						}

					} catch (CorruptIndexException e) {
						// TODOto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
				}
				// Finally all the recommendations are added to the recommendation
				// space
				//System.out.println(">>>>"+recommendationsByItemURI.values());
				for (Recommendation recommendation : recommendationsByItemURI
						.values()) {
					Parameter parameter = new Parameter();
					parameter.setName("technique");
					parameter.setValue("keyword-based");
					recommendation.getProvenance().getParameters().add(parameter);
					recommedationSpace.addRecommendationForUser(user,
							recommendation);
				}
			}
			
		
		}

	}

	private float _scoreAverage(ScoreDoc[] scoreDocs) {
		float average = 0;
		for (ScoreDoc scoreDoc : scoreDocs) {
			average += scoreDoc.score;

		}
		return average / scoreDocs.length;
	}

	private float _scoreMax(ScoreDoc[] scoreDocs) {
		float max = 0;
		for (ScoreDoc scoreDoc : scoreDocs) {
			if (scoreDoc.score > max)
				max = scoreDoc.score;

		}
		return max;
	}

	public void init(Model model, Properties inizializationProperties) {
		this.model = model;
this.initializationProperties=inizializationProperties;
		this.parser = null;
		try {
			String indexDirectory = this.initializationProperties.getProperty(WorkflowsKeywordContentBasedRecommender.INDEX_PATH_PROPERTY);
			Directory dir = FSDirectory.open(new File(indexDirectory)); // 3
			this.is = new IndexSearcher(dir);
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

	private ArrayList<ArrayList<String>> _determineQueryTerms(User user) {
		ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();

		ArrayList<Tagging> orderedTags = _orderByFrequency(user.getTagApplied());
		if (orderedTags.size() == 1) {
			String queryExpression = "contents:" + orderedTags.get(0).getTag();
			ArrayList<String> queryExpressions = new ArrayList<String>();
			queryExpressions.add(queryExpression);
			queries.add(queryExpressions);
		} else if (orderedTags.size() == 2) {
			String queryExpressionA = "contents:" + orderedTags.get(0).getTag();
			String queryExpressionB = "contents:" + orderedTags.get(1).getTag();

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
			String queryExpressionA = "contents:" + orderedTags.get(0).getTag();
			String queryExpressionB = "contents:" + orderedTags.get(1).getTag();
			String queryExpressionC = "contents:" + orderedTags.get(2).getTag();

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

	public String _buildQuery(ArrayList<String> terms) {
		//System.out.println("Este es el que entra " + terms);
		Iterator<String> termsIt = terms.iterator();
		String queryExpression = termsIt.next();
		while (termsIt.hasNext()) {

			queryExpression = queryExpression + " and " + termsIt.next();
		}

		return queryExpression;
	}

	public void close() {
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<Tagging> _orderByFrequency(ArrayList<Tagging> taggingsList) {
		ArrayList<Tagging> taggingsListOrdered = (ArrayList<Tagging>) taggingsList
				.clone();
		Collections.sort(taggingsListOrdered);
		Collections.reverse(taggingsListOrdered);
		return taggingsListOrdered;

	}

}
