package epnoi.recommeders;

import epnoi.model.parameterization.RecommenderParameters;

public class RecommendersFactory {
	public static Recommender buildRecommender(String type) {
		if (type.equals(Recommender.WORKFLOWS_COLLABORATIVE_FILTER)) {
			return new WorkflowsCollaborativeFilterRecommender();
		} else if (type.equals(Recommender.FILES_COLLABORATIVE_FILTER)) {
			return new FilesCollaborativeFilterRecommender();
		} else if (type.equals(Recommender.KEYWORD_CONTENT_BASED)) {
			return new WorkflowsKeywordContentBasedRecommender();
		}
		return null;
	}

	public static Recommender buildRecommender(
			RecommenderParameters recommenderParameters) {
		if (recommenderParameters.getType().equals(
				Recommender.WORKFLOWS_COLLABORATIVE_FILTER)) {

			return new WorkflowsCollaborativeFilterRecommender();
		} else if (recommenderParameters.getType().equals(
				Recommender.FILES_COLLABORATIVE_FILTER)) {
			return new FilesCollaborativeFilterRecommender();

		} else if (recommenderParameters.getType().equals(
				Recommender.KEYWORD_CONTENT_BASED)) {
			return new WorkflowsKeywordContentBasedRecommender();
		}
		return null;
	}
}