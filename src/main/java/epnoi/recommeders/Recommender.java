package epnoi.recommeders;

import epnoi.core.EpnoiCore;
import epnoi.model.parameterization.RecommenderParameters;

public interface Recommender {
	//These constant are used specially in the configuration file. They are not used for the provenance information!
	public static final String WORKFLOWS_COLLABORATIVE_FILTER = "WORKFLOWS_COLLABORATIVE_FILTER";
	public static final String FILES_COLLABORATIVE_FILTER = "FILES_COLLABORATIVE_FILTER";
	//public static final String KEYWORD_CONTENT_BASED = "KEYWORD_CONTENT_BASED";
	public static final String WORKFLOWS_KEYWORD_CONTENT_BASED = "WORKFLOWS_KEYWORD_CONTENT_BASED";
	public static final String EXTERNAL_RESOURCES_KEYWORD_CONTENT_BASED = "EXTERNAL_RESOURCES_KEYWORD_CONTENT_BASED";
	public static final String USERS_SOCIAL_NETWORK = "USER_SOCIAL_NETWORK";
	public static final String WORKFLOWS_GROUP_BASED = "WORKFLOWS_GROUP_BASED";
	
	public static final String SIMILARITY_EUCLIDEAN = "SIMILARITY_EUCLIDEAN";
	public static final String SIMILARITY_PEARSON_CORRELATION = "SIMILARITY_PEARSON_CORRELATION";

	public static final String NEIGHBOURHOOD_TYPE_NEAREST = "NEIGHBOURHOOD_TYPE_NEAREST";
	public static final String NEIGHBOURHOOD_TYPE_THRESHOLD = "NEIGHBOURHOOD_TYPE_THRESHOLD";

	
	public void init(EpnoiCore epnoiCore);

	public void close();

	public RecommenderParameters getInitializationParameters();
}
