package epnoi.recommeders;

import java.util.Map;

import epnoi.model.RecommendationSpace;


public interface OnTheFlyRecommender extends Recommender {
	public static final String USER_URI_PARAMETER="USER_URI";
	public static final String PACK_URI_PARAMETER="PACK_URI";
	public static final String USER_PARAMETER="USER";
	public static final String PACK_PARAMETER="PACK";
	
	public static final String RECOMMENDATION_TECHNIQUE_PARAMETER = "RECO_TECH";
	
	public void recommend(RecommendationSpace recommedationSpace, Map<String, Object> parameters);

}
