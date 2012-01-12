package epnoi.recommeders;

public class RecommendersFactory {
	public static Recommender buildRecommender(String type) {
		if (type.equals(Recommender.WORKFLOWS_COLLABORATIVE_FILTER)) {
			return new WorkflowsCollaborativeFilterRecommender();
		}else if (type.equals(Recommender.FILES_COLLABORATIVE_FILTER)){
		return new FilesCollaborativeFilterRecommender();
		}else if (type.equals(Recommender.KEYWORD_CONTENT_BASED)){
			return new WorkflowsKeywordContentBasedRecommender();
		}
		return null;
	}	
}
