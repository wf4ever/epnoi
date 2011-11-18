package epnoi.core;

import java.util.ArrayList;

import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.recommeders.CollaborativeFilterRecommender;
import epnoi.recommeders.KeywordContentBasedRecommender;
import epnoi.recommeders.Recommender;
import epnoi.recommeders.RecommendersFactory;

public class EpnoiCore {
	private String MODEL_PATH = "/myExperimentModel.xml";
	Model model = null;

	CollaborativeFilterRecommender workflowsCollaborativeFilteringRecommender = null;
	CollaborativeFilterRecommender filesCollaborativeFilteringRecommender = null;
	KeywordContentBasedRecommender kewyordContentBasedRecommender = null;

	RecommendationSpace recommendationSpace;

	public void init() {
		this.model = ModelReader.read(MODEL_PATH);

		this._initRecommeders();
		this._initRecommendationSpace();

	}

	public void init(String filePath) {
		this.model = ModelReader.read(filePath);

		this._initRecommeders();
		this._initRecommendationSpace();
	}

	private void _initRecommendationSpace() {
		this.recommendationSpace = new RecommendationSpace();
		// All the recommenders offer the firs round of recommendations
	/*
		this.workflowsCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
		this.filesCollaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
		*/
		this.kewyordContentBasedRecommender.recommend(this.recommendationSpace);
	}

	private void _initRecommeders() {
		this.workflowsCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.WORKFLOWS_COLLABORATIVE_FILTER);
		this.workflowsCollaborativeFilteringRecommender.init(this.model);
		this.filesCollaborativeFilteringRecommender = (CollaborativeFilterRecommender) RecommendersFactory
				.buildRecommender(Recommender.FILES_COLLABORATIVE_FILTER);
		this.filesCollaborativeFilteringRecommender.init(this.model);
		this.kewyordContentBasedRecommender = (KeywordContentBasedRecommender) RecommendersFactory
				.buildRecommender(Recommender.KEYWORD_CONTENT_BASED);
		this.kewyordContentBasedRecommender.init(this.model);
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
