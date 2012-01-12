package epnoi.core;

import java.util.ArrayList;
import java.util.Properties;

import epnoi.inferenceengine.InferenceEngine;
import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.recommeders.CollaborativeFilterRecommender;
import epnoi.recommeders.Recommender;
import epnoi.recommeders.RecommendersFactory;
import epnoi.recommeders.WorkflowsKeywordContentBasedRecommender;

public class EpnoiCore {
	
	public static final String MODEL_PATH_PROPERTY = "model.path";
	public static final String INDEX_PATH_PROPERTY = "index.path";
	Model model = null;
	InferenceEngine inferenceEngine=null;

	CollaborativeFilterRecommender workflowsCollaborativeFilteringRecommender = null;
	CollaborativeFilterRecommender filesCollaborativeFilteringRecommender = null;
	WorkflowsKeywordContentBasedRecommender kewyordContentBasedRecommender = null;

	RecommendationSpace recommendationSpace;
	private Properties initializationProperties = null;

	public void init(Properties initializationProperties) {
		this.initializationProperties = initializationProperties;
		String modelPath = this.initializationProperties.getProperty(EpnoiCore.MODEL_PATH_PROPERTY);
		System.out.println("-----------------------------------------The model is in "+modelPath);
		this.model = ModelReader.read(modelPath);

		this._initRecommeders();
		this._initRecommendationSpace();

	}

	private void _initRecommendationSpace() {
		this.recommendationSpace = new RecommendationSpace();
		// All the recommenders offer the first round of recommendations
	
		this.workflowsCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
		this.filesCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
				this.kewyordContentBasedRecommender.recommend(this.recommendationSpace);
	}

	private void _initRecommeders() {
		//Workflow collaborative filtering recommender
		this.workflowsCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.WORKFLOWS_COLLABORATIVE_FILTER);
		this.workflowsCollaborativeFilteringRecommender.init(this.model, new Properties());
		
		//Files collaborative filtering recommender
		this.filesCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.FILES_COLLABORATIVE_FILTER);
		this.filesCollaborativeFilteringRecommender.init(this.model, new Properties());
		
		//Workflow keyword based recommender
		this.kewyordContentBasedRecommender = (WorkflowsKeywordContentBasedRecommender) RecommendersFactory
				.buildRecommender(Recommender.KEYWORD_CONTENT_BASED);
		Properties workflowKeywordContentBasedProperties = new Properties();
		workflowKeywordContentBasedProperties.setProperty(WorkflowsKeywordContentBasedRecommender.INDEX_PATH_PROPERTY, this.initializationProperties.getProperty(EpnoiCore.INDEX_PATH_PROPERTY));
		this.kewyordContentBasedRecommender.init(this.model, workflowKeywordContentBasedProperties);
	}

	public ArrayList<Recommendation> getRecommendationsForUser(Long userID) {
		return null;
	}

	public RecommendationSpace getRecommendationSpace() {
		return recommendationSpace;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
