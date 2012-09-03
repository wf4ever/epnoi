package epnoi.core;

import java.util.HashMap;
import java.util.logging.Logger;

import epnoi.inferenceengine.InferenceEngine;
import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.RecommendationSpace;
import epnoi.model.parameterization.CollaborativeFilterRecommenderParameters;
import epnoi.model.parameterization.KeywordRecommenderParameters;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.SocialNetworkRecommenderParameters;
import epnoi.recommeders.CollaborativeFilterRecommender;
import epnoi.recommeders.KeywordContentBasedRecommender;
import epnoi.recommeders.Recommender;
import epnoi.recommeders.RecommendersFactory;
import epnoi.recommeders.UsersSocialNetworkRecommender;
import epnoi.recommeders.WorkflowsKeywordContentBasedRecommender;

public class EpnoiCore {

	private static final Logger logger = Logger.getLogger(EpnoiCore.class
			.getName());

	public static final String MODEL_PATH_PROPERTY = "model.path";
	public static final String INDEX_PATH_PROPERTY = "index.path";
	Model model = null;
	InferenceEngine inferenceEngine = null;

	CollaborativeFilterRecommender workflowsCollaborativeFilteringRecommender = null;
	CollaborativeFilterRecommender filesCollaborativeFilteringRecommender = null;
	WorkflowsKeywordContentBasedRecommender kewyordContentBasedRecommender = null;

	RecommendationSpace recommendationSpace;
	RecommendationSpace inferredRecommendationSpace;

	private HashMap<String, Recommender> recommenders;

	private ParametersModel parametersModel = null;

	/**
	 * The initialization method for the epnoiCore
	 * 
	 * @param initializationProperties
	 *            The properties that define the characteristics of the
	 *            epnoiCore.
	 */

	public void init(ParametersModel parametersModel) {
		logger.info("Initializing the epnoi core");
		this.parametersModel = parametersModel;
		String modelPath = this.parametersModel.getModelPath();
		this.model = ModelReader.read(modelPath);
		this.recommenders = new HashMap<String, Recommender>();

		this._initRecommenders();
		this._initRecommendationSpace();

		this._initInferenceEngine();

		this._initInferredRecommendationSpace();
	}

	/**
	 * Inference engine initialization
	 */
	private void _initInferenceEngine() {
		logger.info("Initializing the Inference Engine");
		this.inferenceEngine = new InferenceEngine(this.parametersModel);
		this.inferenceEngine.init(model);
	}

	/**
	 * Recommendation space intialization
	 */
	private void _initRecommendationSpace() {
		logger.info("Initializing the Recommendations Space");

		this.recommendationSpace = new RecommendationSpace();

		for (Recommender recommender : this.recommenders.values()) {
			recommender.recommend(this.recommendationSpace);
		}
		logger.info("The recommendation space has been initialized with "+this.recommendationSpace.getAllRecommendations().size()+" recommendations");
	}

	/**
	 * Inferred recommendation space initialization. This method asumes that the
	 * recommendation space has been already initialized
	 */
	private void _initInferredRecommendationSpace() {
		logger.info("Initializing the inferred recommendations space");

		this.inferredRecommendationSpace = this.inferenceEngine
				.infer(this.recommendationSpace);
		logger.info("The inferred recommend space has been initialized with "
				+ this.inferredRecommendationSpace.getAllRecommendations().size());
	}

	/**
	 * Recommenders initialization
	 */

	private void _initRecommenders() {
		logger.info("Initializing recommenders");

		for (CollaborativeFilterRecommenderParameters collaborativeFilterRecommenderParameters : this.parametersModel
				.getCollaborativeFilteringRecommender()) {

			CollaborativeFilterRecommender collaborativeFilterRecommender = (CollaborativeFilterRecommender) RecommendersFactory
					.buildRecommender(collaborativeFilterRecommenderParameters,
							parametersModel);
			collaborativeFilterRecommender.init(model);
			this.recommenders.put(
					collaborativeFilterRecommenderParameters.getURI(),
					collaborativeFilterRecommender);

		}

		// Workflow keyword based recommender
		for (KeywordRecommenderParameters keyword : this.parametersModel
				.getKeywordBasedRecommender()) {
			KeywordContentBasedRecommender keywordContentBasedRecommender = (KeywordContentBasedRecommender) RecommendersFactory
					.buildRecommender(keyword, parametersModel);
			keywordContentBasedRecommender.init(model);
			this.recommenders.put(keyword.getURI(),
					keywordContentBasedRecommender);

		}

		for (SocialNetworkRecommenderParameters socialNetowrkRecommenderParameters : this.parametersModel
				.getSocialRecommender()) {
			UsersSocialNetworkRecommender userSocialRecommender = (UsersSocialNetworkRecommender) RecommendersFactory
					.buildRecommender(socialNetowrkRecommenderParameters,
							parametersModel);
			userSocialRecommender.init(model);
			this.recommenders.put(socialNetowrkRecommenderParameters.getURI(),
					userSocialRecommender);
		}

		String logMessage = "The following recommenders have been initialized: \n";
		for (Recommender recommender : this.recommenders.values()) {
			logMessage += "	> ("
					+ recommender.getInitializationParameters().getURI();

			logMessage += recommender.getInitializationParameters() + "\n";

		}
		logger.info(logMessage);

	}

	public RecommendationSpace getRecommendationSpace() {
		return recommendationSpace;
	}

	/**
	 * Inener model getter
	 * 
	 * @return The inner model of the recommender
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * 
	 * @param model
	 *            The inner model that the recommender would use
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	public void close() {
		for (Recommender recommender : this.recommenders.values()) {
			recommender.close();
		}
	
	}

	public RecommendationSpace getInferredRecommendationSpace() {
		return inferredRecommendationSpace;
	}

	public void setInferredRecommendationSpace(
			RecommendationSpace inferredRecommendationSpace) {
		this.inferredRecommendationSpace = inferredRecommendationSpace;
	}
}
