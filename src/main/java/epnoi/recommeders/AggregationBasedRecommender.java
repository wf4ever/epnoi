package epnoi.recommeders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

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
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import wrappers.myexperiment.PackWrapper;
import epnoi.core.EpnoiCore;
import epnoi.model.ContextModel;
import epnoi.model.Explanation;
import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Pack;
import epnoi.model.Parameter;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationContext;
import epnoi.model.RecommendationSpace;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.AggregationBasedRecommenderParameters;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.RecommenderParameters;

public class AggregationBasedRecommender implements OnTheFlyRecommender {
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
	private static final Logger logger = Logger
			.getLogger(WorkflowsGroupBasedRecommender.class.getName());
	private static final int MAX_TOKEN_LENGTH = 12;

	private static final int MIN_TOKEN_LENGTH = 2;

	Model model = null;
	ContextModel contextModel = null;
	Directory directory = null;
	IndexSearcher indexSearcher = null;
	QueryParser parser = null;
	ParametersModel parametersModel = null;
	AggregationBasedRecommenderParameters recommenderParameters = null;

	AggregationBasedRecommender(RecommenderParameters initializationParameters,
			ParametersModel parametersModel) {
		this.parametersModel = parametersModel;
		this.recommenderParameters = (AggregationBasedRecommenderParameters) initializationParameters;

	}

	private AggregationBasedRecommender() {
		// System.out.println("ENTRA!");
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void init(EpnoiCore epnoiCore) {

		this.model = epnoiCore.getModel();
		this.contextModel = epnoiCore.getContextModel();

		this.parser = null;
		try {
			System.out.println("-------->" + this.recommenderParameters);
			String indexDirectory = this.recommenderParameters.getIndexPath();
			logger.info("Index directory for the recommender" + indexDirectory);
			Directory dir = FSDirectory.open(new java.io.File(indexDirectory));
			this.indexSearcher = new IndexSearcher(dir);
			this.parser = new QueryParser(Version.LUCENE_30, "contents",
					new StandardAnalyzer(Version.LUCENE_30));

		} catch (CorruptIndexException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void close() {
		try {
			indexSearcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void recommend(RecommendationSpace recommendationSpace, Map<String, Object> parameters) {
		String packURI = (String)parameters.get(OnTheFlyRecommender.PACK_URI_PARAMETER);
		String userURI = (String)parameters.get(OnTheFlyRecommender.USER_URI_PARAMETER);
		
		ArrayList<String> packKeywords = null;
		//Pack extraction is made on the fly (meter un flag?)
		Pack pack = PackWrapper.extractPack(packURI);
		User user = this.model.getUserByURI(userURI);
		
		
		packKeywords = _scanPackKewyords(pack);

		System.out
				.println("//////////////////////////////////////////////////////////"
						+ _buildQuery(packKeywords));

		System.out.println("The keywords are >" + packKeywords
				+ "the built query is " + _buildQuery(packKeywords));
		String queryExpression = _buildQuery(packKeywords);

		ArrayList<Recommendation> recommendations = new ArrayList<Recommendation>();
		float maximumScore = 0.f;
		try {
			Query query = parser.parse(queryExpression);

			TopDocs topHits = indexSearcher.search(query,
					this.recommenderParameters.getNumberOfQueryHits());
			/*
			 * System.out.println("(q:" + queryExpression +
			 * ") Recommendations for user " + user.getName() + " #> " +
			 * hits.totalHits);
			 */

			// Each of the top hits correspond to a recommendation.

			for (ScoreDoc scoreDocument : topHits.scoreDocs) {

				// System.out.print(">" + scoreDocument.+" ");
				Document document = indexSearcher.doc(scoreDocument.doc);
				// System.out.println(doc.get("filename"));

				String itemURI = document.get("filename");
				// System.out.println("itemURI recomendado --> " + itemURI +
				// " "
				// + scoreDocument.score + " | "
				// + workflow.getUploaderURI());

				if (!pack.getInternalWorkflows().contains(itemURI)) {

					if (scoreDocument.score > maximumScore) {
						maximumScore = scoreDocument.score;
					}

					Workflow workflow = this.model.getWorkflowsByURI().get(
							itemURI);
					Recommendation newRecommendation = new Recommendation();
					newRecommendation
							.setRecommenderURI(this.recommenderParameters
									.getURI());

					newRecommendation.setItemURI(itemURI);
					newRecommendation.setStrength(scoreDocument.score);
					String explanationText = "The workflow entitled "
							+ workflow.getTitle()
							+ ("(URI:")
							+ workflow.getURI()
							+ ") is recommended to you since you selected the resources ("

							+ ") with similar components ";

					Explanation explanation = new Explanation();

					explanation.setExplanation(explanationText);
					explanation.setTimestamp(new Date(System
							.currentTimeMillis()));
					newRecommendation.setExplanation(explanation);

					Parameter parameterTechnique = new Parameter();
					parameterTechnique.setName(Provenance.TECHNIQUE);
					parameterTechnique
							.setValue(Provenance.TECHNIQUE_AGGREGATION_CONTENT_BASED);
					Parameter parameter = new Parameter();

					parameter.setName(Provenance.ITEM_TYPE);
					parameter.setValue(Provenance.ITEM_TYPE_WORKFLOW);

					newRecommendation.getProvenance().getParameters()
							.add(parameterTechnique);
					newRecommendation.getProvenance().getParameters()
							.add(parameter);

					recommendations.add(newRecommendation);

				}

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
		recommendationSpace.addRecommendationsForPack(pack,
				_normalizeStrength(recommendations, maximumScore));
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

	private void _generateUserKeywords(String resourceURI,
			ArrayList<String> keywords) {

		User user = this.model.getUserByURI(resourceURI);

		for (String workflowURI : user.getWorkflows()) {

			_generateWorkflowKeywords(workflowURI, keywords);
		}

		for (String fileURI : user.getFiles()) {
			_generateFileKeywords(fileURI, keywords);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public String _buildQuery(ArrayList<String> terms) {
		// System.out.println("Este es el que entra " + terms);
		Iterator<String> termsIt = terms.iterator();
		String queryExpression = "contents:" + termsIt.next();
		while (termsIt.hasNext()) {

			queryExpression = queryExpression + " or " + "contents:"
					+ termsIt.next();
		}
		// System.out.println("----->" + queryExpression);
		return queryExpression;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public RecommenderParameters getInitializationParameters() {
		return this.recommenderParameters;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	private ArrayList<Tagging> _orderByFrequency(ArrayList<Tagging> taggingsList) {
		ArrayList<Tagging> taggingsListOrdered = (ArrayList<Tagging>) taggingsList
				.clone();
		Collections.sort(taggingsListOrdered);
		Collections.reverse(taggingsListOrdered);
		return taggingsListOrdered;

	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "WorkflowsGroupBasedRecommender"
				+ this.getInitializationParameters().getURI();
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

	private ArrayList<String> _scanKeywords(String resourceURI) {
		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, resourceURI);
		InputStream is = null;
		ContentHandler handler = null;
		try {
			is = new URL(resourceURI).openStream();

			Parser parser = new AutoDetectParser();
			handler = new BodyContentHandler(-1);

			ParseContext context = new ParseContext();
			context.set(Parser.class, parser);

			parser.parse(is, handler, metadata, new ParseContext());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * String[] tokens = handler.toString().split(delims); for (String token
		 * : tokens) { System.out.println(">>> " + token); }
		 */

		StringTokenizer stringTokenizer = new StringTokenizer(
				handler.toString());

		ArrayList<String> candidateKeywords = new ArrayList<String>();

		while (stringTokenizer.hasMoreTokens()) {

			String token = stringTokenizer.nextToken();
			// token = token.replace(".", "");
			// token = token.replace(",", "");
			token = token.replaceAll("[^a-zA-Z 0-9]+", "");

			if (!stopWordsList.contains(token.toLowerCase())) {

				if (token.matches("[\\w]*[a-zA-Z]+[\\w]*")
						&& token.length() > MIN_TOKEN_LENGTH
						&& token.length() < MAX_TOKEN_LENGTH) {
					// System.out.println("Este si!!");
					System.out.println("---> " + token);
					if (!candidateKeywords.contains(token))
						candidateKeywords.add(token);
				}

			}

		}
		return candidateKeywords;
	}

	private ArrayList<String> _scanPackKewyords(Pack pack) {

		ArrayList<String> packKeywords = new ArrayList<String>();
		for (String workflowURI : pack.getInternalWorkflows()) {
			/*
			 * System.out .println(workflowURI +
			 * "//////////////////////////////////////////////////////////////////////////////////////////////"
			 * );
			 */
			ArrayList<String> keywords = _scanKeywords(workflowURI);
			for (String keyword : keywords) {
				// System.out.println("---->" + keyword);
				keyword = keyword.toLowerCase();
				if (!packKeywords.contains(keyword)) {
					packKeywords.add(keyword);
				}
			}

		}

		for (String fileURI : pack.getInternalFiles()) {
			ArrayList<String> keywords = _scanKeywords(fileURI);
			// System.out.println("fileuri "+fileURI);
			for (String keyword : keywords) {
				System.out.println("---->" + keyword);
				keyword = keyword.toLowerCase();
				if (!packKeywords.contains(keyword)) {
					packKeywords.add(keyword);
				}
			}
		}
		return packKeywords;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {

		String delims = "\\s+";
		// String url = "http://www.slashdot.org/";
		// String url = "http://lingo.stanford.edu/sag/papers/copestake.pdf";
		String packURI = "http://www.myexperiment.org/pack.xml?id=122";
		String url = "file:///Users/rafita/Desktop/D4.2v1.4_jun.docx	ppc.txt";
		ArrayList<String> packKeywords = new ArrayList<String>();
		AggregationBasedRecommender recommender = new AggregationBasedRecommender();
		Pack pack = PackWrapper.extractPack(packURI);
		packKeywords = recommender._scanPackKewyords(pack);

		System.out
				.println("//////////////////////////////////////////////////////////"
						+ recommender._buildQuery(packKeywords));
	}

}
