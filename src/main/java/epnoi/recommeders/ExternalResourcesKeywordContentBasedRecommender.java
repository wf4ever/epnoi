package epnoi.recommeders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.KeywordRecommenderParameters;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.RecommenderParameters;

public class ExternalResourcesKeywordContentBasedRecommender implements
		KeywordContentBasedRecommender {
	private static final String[] stopWords = { "a", "about", "above", "above",
			"across", "after", "afterwards", "again", "against", "all",
			"almost", "alone", "along", "already", "also", "although",
			"always", "am", "among", "amongst", "amoungst", "amount", "an",
			"and", "another", "any", "anyhow", "anyone", "anything", "anyway",
			"anywhere", "are", "around", "as", "at", "back", "be", "became",
			"because", "become", "becomes", "becoming", "been", "before",
			"beforehand", "behind", "being", "below", "beside", "besides",
			"between", "beyond", "bill", "both", "bottom", "but", "by", "call",
			"can", "cannot", "cant", "co", "con", "could", "couldnt", "cry",
			"de", "describe", "detail", "do", "done", "down", "due", "during",
			"each", "eg", "eight", "either", "eleven", "else", "elsewhere",
			"empty", "enough", "etc", "even", "ever", "every", "everyone",
			"everything", "everywhere", "except", "few", "fifteen", "fify",
			"fill", "find", "fire", "first", "five", "for", "former",
			"formerly", "forty", "found", "four", "from", "front", "full",
			"further", "get", "give", "go", "had", "has", "hasnt", "have",
			"he", "hence", "her", "here", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "him", "himself", "his", "how",
			"however", "hundred", "ie", "if", "in", "inc", "indeed",
			"interest", "into", "is", "it", "its", "itself", "keep", "last",
			"latter", "latterly", "least", "less", "ltd", "made", "many",
			"may", "me", "meanwhile", "might", "mill", "mine", "more",
			"moreover", "most", "mostly", "move", "much", "must", "my",
			"myself", "name", "namely", "neither", "never", "nevertheless",
			"next", "nine", "no", "nobody", "none", "noone", "nor", "not",
			"nothing", "now", "nowhere", "of", "off", "often", "on", "once",
			"one", "only", "onto", "or", "other", "others", "otherwise", "our",
			"ours", "ourselves", "out", "over", "own", "part", "per",
			"perhaps", "please", "put", "rather", "re", "same", "see", "seem",
			"seemed", "seeming", "seems", "serious", "several", "she",
			"should", "show", "side", "since", "sincere", "six", "sixty", "so",
			"some", "somehow", "someone", "something", "sometime", "sometimes",
			"somewhere", "still", "such", "system", "take", "ten", "than",
			"that", "the", "their", "them", "themselves", "then", "thence",
			"there", "thereafter", "thereby", "therefore", "therein",
			"thereupon", "these", "they", "thickv", "thin", "third", "this",
			"those", "though", "three", "through", "throughout", "thru",
			"thus", "to", "together", "too", "top", "toward", "towards",
			"twelve", "twenty", "two", "un", "under", "until", "up", "upon",
			"us", "very", "via", "was", "we", "well", "were", "what",
			"whatever", "when", "whence", "whenever", "where", "whereafter",
			"whereas", "whereby", "wherein", "whereupon", "wherever",
			"whether", "which", "while", "whither", "who", "whoever", "whole",
			"whom", "whose", "why", "will", "with", "within", "without",
			"would", "yet", "you", "your", "yours", "yourself", "yourselves",
			"the" };
	private static final List<String> stopWordsList = Arrays.asList(stopWords);

	Model model = null;

	Directory directory = null;
	IndexSearcher indexSearcher = null;
	QueryParser parser = null;
	ParametersModel parametersModel = null;

	private KeywordRecommenderParameters recommenderParameters;

	public ExternalResourcesKeywordContentBasedRecommender(
			RecommenderParameters initializationParameters,
			ParametersModel parametersModel) {
		this.recommenderParameters = (KeywordRecommenderParameters) initializationParameters;
		this.parametersModel = parametersModel;
	}

	// -------------------------------------------------------------------------------------------------

	public void recommend(RecommendationSpace recommendationSpace) {

		try {
			// System.out.println("......>"+this.recommenderParameters.getIndexPath());
			Directory dir = FSDirectory.open(new java.io.File(
					this.recommenderParameters.getIndexPath()));
			this.indexSearcher = new IndexSearcher(dir);
			this.parser = new QueryParser(Version.LUCENE_30, "contents",
					new StandardAnalyzer(Version.LUCENE_30));
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (User user : model.getUsers()) {
			if (user.getID() < 20) {
				System.out.println("User " + user.getName());
				ArrayList<String> keywords = new ArrayList<String>();
				this._generateUserKeywords(user, keywords);

				System.out.println("The keywords are >" + keywords
						+ "the built query is " + _buildQuery(keywords));
				String queryExpression = _buildQuery(keywords);
				if (!"".equals(queryExpression)) {
					ArrayList<Recommendation> recommendations = new ArrayList<Recommendation>();
					float maximumScore = 0.f;
					try {
						Query query = this.parser.parse(queryExpression);

						TopDocs topHits = indexSearcher.search(query,
								this.recommenderParameters
										.getNumberOfQueryHits());
						/*
						 * System.out.println("(q:" + queryExpression +
						 * ") Recommendations for user " + user.getName() +
						 * " #> " + hits.totalHits);
						 */

						// Each of the top hits correspond to a recommendation.

						for (ScoreDoc scoreDocument : topHits.scoreDocs) {

							// System.out.print(">" + scoreDocument.+" ");
							Document document = indexSearcher
									.doc(scoreDocument.doc);
							// System.out.println(doc.get("filename"));

							String itemURI = document.get("uri");
							//System.out.println("itemURI recomendado --> "
								//	+ itemURI + " | " + scoreDocument.score);

							if (scoreDocument.score > maximumScore) {
								maximumScore = scoreDocument.score;
							}

							
							Recommendation newRecommendation = new Recommendation();
							newRecommendation
									.setRecommenderURI(this.recommenderParameters
											.getURI());

							newRecommendation.setItemURI(itemURI);
							
							newRecommendation.setStrength(scoreDocument.score);
							String explanationText = "The external resource "+itemURI+" is recommended for you since the keywords that define your interest and they partially describe its content";
							/*
							 * + workflow.getTitle() + ("(URI:") +
							 * workflow.getURI() +
							 * ") is recommended to you since you selected the resources ("
							 * + recommendationContext.getResource() +
							 * ") with similar components ";
							 */
							Explanation explanation = new Explanation();

							explanation.setExplanation(explanationText);
							explanation.setTimestamp(new Date(System
									.currentTimeMillis()));
							newRecommendation.setExplanation(explanation);

							Parameter parameterTechnique = new Parameter();
							parameterTechnique.setName(Provenance.TECHNIQUE);
							parameterTechnique
									.setValue(Provenance.TECHNIQUE_KEYWORD_CONTENT_BASED);
							Parameter parameter = new Parameter();

							parameter.setName(Provenance.ITEM_TYPE);
							parameter
									.setValue(Provenance.ITEM_TYPE_EXTERNAL_RESOURCE);

							newRecommendation.getProvenance().getParameters()
									.add(parameterTechnique);
							newRecommendation.getProvenance().getParameters()
									.add(parameter);
							//System.out.println("the recommender > "+newRecommendation);

							recommendations.add(newRecommendation);

						}
					} catch (CorruptIndexException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					recommendationSpace.addRecommendationsForUser(user,
							_normalizeStrength(recommendations, maximumScore));
				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------

	public void init(EpnoiCore epnoiCore) {
		this.model = epnoiCore.getModel();

		this.parser = null;
		try {
			String indexDirectory = this.recommenderParameters.getIndexPath();

			Directory dir = FSDirectory.open(new java.io.File(indexDirectory)); // 3
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

	public RecommenderParameters getInitializationParameters() {
		return this.recommenderParameters;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private void _generateWorkflowKeywords(String resourceURI,
			ArrayList<String> keywords) {
		Workflow workflow = this.model.getWorkflowsByURI().get(resourceURI);

		for (String tag : workflow.getTags()) {
			keywords.add(tag);
		}

		if (workflow.getTitle() != null) {
			String title = workflow.getTitle();
			_addCarefully(keywords, _tokenizeAndClean(title));

		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void _addCarefully(ArrayList<String> keywords,
			ArrayList<String> candidateKeywords) {
		for (String candidateKeyword : candidateKeywords) {
			candidateKeyword = candidateKeyword.toLowerCase();
			candidateKeyword = candidateKeyword
					.replaceAll("[^a-zA-Z 0-9]+", "");

			if (!keywords.contains(candidateKeyword)) {
				keywords.add(candidateKeyword);
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private void _generateFileKeywords(String resourceURI,
			ArrayList<String> keywords) {
		File file = this.model.getFileByURI(resourceURI);
		/*
		 * Files info should be completed with tags! for (String tag :
		 * workflow.getTags()) { keywords.add(tag); }
		 */
		if (file.getTitle() != null) {
			String title = file.getTitle();
			_addCarefully(keywords, _tokenizeAndClean(title));

		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private void _generateUserKeywords(User user, ArrayList<String> keywords) {

		for (String workflowURI : user.getWorkflows()) {

			_generateWorkflowKeywords(workflowURI, keywords);
		}

		for (String fileURI : user.getFiles()) {
			_generateFileKeywords(fileURI, keywords);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public String _buildQuery(ArrayList<String> terms) {
		System.out.println("Este es el que entra " + terms);
		if (terms.size() > 0) {
			Iterator<String> termsIt = terms.iterator();
			String queryExpression = "contents:" + termsIt.next();
			while (termsIt.hasNext()) {

				queryExpression = queryExpression + " or " + "contents:"
						+ termsIt.next();
			}
			// System.out.println("----->" + queryExpression);
			return queryExpression;
		} else {
			return "";
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private ArrayList<String> _tokenizeAndClean(String sentence) {

		sentence.replace(',', ' ');

		StringTokenizer stringTokenizer = new StringTokenizer(sentence);

		ArrayList<String> keywords = new ArrayList<String>();

		while (stringTokenizer.hasMoreTokens()) {

			String token = stringTokenizer.nextToken();
			if (!stopWordsList.contains(token.toLowerCase())) {
				keywords.add(token);
			}

			// keywords.add(token);
		}
		return keywords;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private ArrayList<Recommendation> _normalizeStrength(
			ArrayList<Recommendation> recommendations, float max) {
		for (Recommendation recommendation : recommendations) {
			recommendation
					.setStrength((recommendation.getStrength() / max) * 5);
		}
		return recommendations;
	}
}
