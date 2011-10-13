package epnoi.core;

import java.lang.reflect.Array;
import java.util.ArrayList;

import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.recommeders.CollaborativeFilterRecommender;

public class EpnoiCore {
	private String MODEL_PATH = "/myExperimentModel.xml";
	Model model = null;
	

	CollaborativeFilterRecommender collaborativeFilteringRecommender = null;
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
		this.collaborativeFilteringRecommender
				.recommend(this.recommendationSpace);
	}

	private void _initRecommeders() {
		this.collaborativeFilteringRecommender = new CollaborativeFilterRecommender();
		this.collaborativeFilteringRecommender.init(this.model);

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
