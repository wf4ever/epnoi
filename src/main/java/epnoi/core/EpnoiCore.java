package epnoi.core;

import java.util.HashMap;

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
		this.parametersModel = parametersModel;
		String modelPath = this.parametersModel.getModelPath();
		System.out
				.println("-----------------------------------------The model is in "
						+ modelPath);
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
		this.inferenceEngine = new InferenceEngine();
		this.inferenceEngine.init(model);
	}

	/**
	 * Recommendation space intialization
	 */
	private void _initRecommendationSpace() {
		this.recommendationSpace = new RecommendationSpace();

		for (Recommender recommender : this.recommenders.values()) {
			recommender.recommend(this.recommendationSpace);
		}

		// All the recommenders offer the first round of recommendations
		/*
		 * this.workflowsCollaborativeFilteringRecommender
		 * .recommend(this.recommendationSpace);
		 * this.filesCollaborativeFilteringRecommender
		 * .recommend(this.recommendationSpace);
		 * this.kewyordContentBasedRecommender
		 * .recommend(this.recommendationSpace);
		 */
	}

	/**
	 * Inferred recommendation space initialization. This method asumes that the
	 */
	private void _initInferredRecommendationSpace() {//---------------------------------------------
/*
		this.inferredRecommendationSpace = this.inferenceEngine
				.infer(this.recommendationSpace);
*/
	}

	/**
	 * Recommenders initialization
	 */

	private void _initRecommenders() {

		for (CollaborativeFilterRecommenderParameters collaborativeFilterRecommenderParameters : this.parametersModel
				.getCollaborativeFilteringRecommender()) {

			CollaborativeFilterRecommender collaborativeFilterRecommender = (CollaborativeFilterRecommender) RecommendersFactory
					.buildRecommender(collaborativeFilterRecommenderParameters);
			collaborativeFilterRecommender.init(model);
			this.recommenders.put(
					collaborativeFilterRecommenderParameters.getURI(),
					collaborativeFilterRecommender);

		}
	
		// Workflow keyword based recommender
		for (KeywordRecommenderParameters keyword : this.parametersModel
				.getKeywordBasedRecommender()) {
			KeywordContentBasedRecommender keywordContentBasedRecommender = (KeywordContentBasedRecommender) RecommendersFactory
					.buildRecommender(keyword);
			keywordContentBasedRecommender.init(model);
			this.recommenders.put(keyword.getURI(),
					keywordContentBasedRecommender);

		}
			
		for (SocialNetworkRecommenderParameters socialNetowrkRecommenderParameters:this.parametersModel.getSocialRecommender()){
			UsersSocialNetworkRecommender userSocialRecommender = (UsersSocialNetworkRecommender)RecommendersFactory.buildRecommender(socialNetowrkRecommenderParameters);
			userSocialRecommender.init(model);
			this.recommenders.put(socialNetowrkRecommenderParameters.getURI(),
					userSocialRecommender);
		}
			
			System.out
					.println("The following recommenders have been initialized");
			for (Recommender recommender : this.recommenders.values()) {
				System.out.println("	> ("
						+ recommender.getInitializationParameters().getURI());
				
				System.out.println(recommender.getInitializationParameters());
				
			}
			

		
		
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
	
	public void close(){
		for (Recommender recommender: this.recommenders.values()){
			recommender.close();
		}
	}
}
