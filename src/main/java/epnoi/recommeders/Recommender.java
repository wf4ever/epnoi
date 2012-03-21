package epnoi.recommeders;

import java.util.Properties;

import epnoi.model.Model;
import epnoi.model.RecommendationSpace;

public interface Recommender {
	public static final String WORKFLOWS_COLLABORATIVE_FILTER = "WORKFLOWS_COLLABORATIVE_FILTER"; 
	public static final String FILES_COLLABORATIVE_FILTER = "FILES_COLLABORATIVE_FILTER"; 
	public static final String KEYWORD_CONTENT_BASED = "KEYWORD_CONTENT_BASED";
	
	
	public static final String SIMILARITY_EUCLIDEAN = "SIMILARITY_EUCLIDEAN";
	public static final String SIMILARITY_PEARSON_CORRELATION="SIMILARITY_PEARSON_CORRELATION";
	
	public static final String NEIGHBOURHOOD_TYPE_NEAREST ="NEIGHBOURHOOD_TYPE_NEAREST";
	public static final String NEIGHBOURHOOD_TYPE_THRESHOLD="NEIGHBOURHOOD_TYPE_THRESHOLD";
	
	
	
	
	public void recommend(RecommendationSpace recommedationSpace);
	public void init(Model model, Properties initializationProperties);
	public void close();
}
