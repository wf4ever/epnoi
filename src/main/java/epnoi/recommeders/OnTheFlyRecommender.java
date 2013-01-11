package epnoi.recommeders;

import epnoi.model.RecommendationSpace;

public interface OnTheFlyRecommender extends Recommender {
	public void recommend(RecommendationSpace recommedationSpace, String URI);

}
