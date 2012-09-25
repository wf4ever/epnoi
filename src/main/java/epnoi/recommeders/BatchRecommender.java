package epnoi.recommeders;

import epnoi.model.RecommendationSpace;

public interface BatchRecommender extends Recommender {
	public void recommend(RecommendationSpace recommedationSpace);

}
