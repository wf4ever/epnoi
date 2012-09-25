package epnoi.recommeders;

import epnoi.model.RecommendationSpace;

public interface ContextualizedRecommender extends Recommender {
	public void recommend(RecommendationSpace recommedationSpace, String userURI);

}
