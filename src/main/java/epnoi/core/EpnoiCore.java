package epnoi.core;

import java.util.Properties;

import epnoi.inferenceengine.InferenceEngine;
import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.RecommendationSpace;
import epnoi.model.parameterization.CollaborativeFilterRecommenderParameters;
import epnoi.model.parameterization.ParametersModel;
import epnoi.recommeders.CollaborativeFilterRecommender;
import epnoi.recommeders.Recommender;
import epnoi.recommeders.RecommendersFactory;
import epnoi.recommeders.WorkflowsKeywordContentBasedRecommender;

public class EpnoiCore {

	public static final String MODEL_PATH_PROPERTY = "model.path";
	public static final String INDEX_PATH_PROPERTY = "index.path";
	Model model = null;
	InferenceEngine inferenceEngine = null;

	CollaborativeFilterRecommender workflowsCollaborativeFilteringRecommender = null;
	CollaborativeFilterRecommender filesCollaborativeFilteringRecommender = null;
	WorkflowsKeywordContentBasedRecommender kewyordContentBasedRecommender = null;

	RecommendationSpace recommendationSpace;
	RecommendationSpace inferredRecommendationSpace;

	private Properties initializationProperties = null;
	private ParametersModel parametersModel = null;

	/**
	 * The initialization method for the epnoiCore
	 * 
	 * @param initializationProperties
	 *            The properties that define the characteristics of the
	 *            epnoiCore.
	 */
	public void init(Properties initializationProperties) {
		this.initializationProperties = initializationProperties;
		String modelPath = this.initializationProperties
				.getProperty(EpnoiCore.MODEL_PATH_PROPERTY);
		// System.out.println("-----------------------------------------The model is in "+modelPath);
		this.model = ModelReader.read(modelPath);

		this._initRecommeders();
		this._initRecommendationSpace();

		this._initInferenceEngine();

		this._initInferredRecommendationSpace();
	}

	public void init(ParametersModel parametersModel) {
		this.parametersModel = parametersModel;
		String modelPath = this.parametersModel.getModelPath();
		// System.out.println("-----------------------------------------The model is in "+modelPath);
		this.model = ModelReader.read(modelPath);

		this._initRecommeders();
		this._initRecommendationSpace();

		this._initInferenceEngine();

		this._initInferredRecommendationSpace();
	}

	/**
	 * Inference engine initialization
	 */
	private void _initInferenceEngine() {
		this.inferenceEngine = new InferenceEngine();
		this.inferenceEngine.init(model);
	}

	/**
	 * Recommendation space intialization
	 */
	private void _initRecommendationSpace() {
		this.recommendationSpace = new RecommendationSpace();
		// All the recommenders offer the first round of recommendations

		this.workflowsCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
		this.filesCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
		this.kewyordContentBasedRecommender.recommend(this.recommendationSpace);
	}

	/**
	 * Inferred recommendation space initialization. This method asumes that the
	 */
	private void _initInferredRecommendationSpace() {

		this.inferredRecommendationSpace = this.inferenceEngine
				.infer(this.recommendationSpace);
	}

	/**
	 * Recommenders initialization
	 */

	private void _initRecommeders() {
		// Workflow collaborative filtering recommender
		this.workflowsCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.WORKFLOWS_COLLABORATIVE_FILTER);
		this.workflowsCollaborativeFilteringRecommender.init(this.model,
				new Properties());

		// Files collaborative filtering recommender
		this.filesCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.FILES_COLLABORATIVE_FILTER);
		this.filesCollaborativeFilteringRecommender.init(this.model,
				new Properties());

		// Workflow keyword based recommender
		this.kewyordContentBasedRecommender = (WorkflowsKeywordContentBasedRecommender) RecommendersFactory
				.buildRecommender(Recommender.KEYWORD_CONTENT_BASED);
		Properties workflowKeywordContentBasedProperties = new Properties();
		workflowKeywordContentBasedProperties.setProperty(
				WorkflowsKeywordContentBasedRecommender.INDEX_PATH_PROPERTY,
				this.initializationProperties
						.getProperty(EpnoiCore.INDEX_PATH_PROPERTY));
		this.kewyordContentBasedRecommender.init(this.model,
				workflowKeywordContentBasedProperties);
	}

	private void _initRecommenders() {

		for (CollaborativeFilterRecommenderParameters collaborativeFilterRecommenderParameters : this.parametersModel
				.getCollaborativeFilteringRecommender()) {

			// Workflow collaborative filtering recommender
			this.workflowsCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
					.buildRecommender(collaborativeFilterRecommenderParameters);
			this.workflowsCollaborativeFilteringRecommender.init(this.model,
					new Properties());
		}

	///Este deberia estar en el nearfuture cubierto por lo anterior
		 this.filesCollaborativeFilteringRecommender =
		 (CollaborativeFilterRecommender) RecommendersFactory.buildRecommender(Recommender.FILES_COLLABORATIVE_FILTER);
		 this.filesCollaborativeFilteringRecommender.init(this.model, new
		 Properties());
		 
		// Workflow keyword based recommender
		this.kewyordContentBasedRecommender = (WorkflowsKeywordContentBasedRecommender) RecommendersFactory
				.buildRecommender(Recommender.KEYWORD_CONTENT_BASED);
		Properties workflowKeywordContentBasedProperties = new Properties();
		workflowKeywordContentBasedProperties.setProperty(
				WorkflowsKeywordContentBasedRecommender.INDEX_PATH_PROPERTY,
				this.initializationProperties
						.getProperty(EpnoiCore.INDEX_PATH_PROPERTY));
		this.kewyordContentBasedRecommender.init(this.model,
				workflowKeywordContentBasedProperties);
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
}
