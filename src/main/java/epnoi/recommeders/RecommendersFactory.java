package epnoi.recommeders;

import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.RecommenderParameters;

public class RecommendersFactory {

	public static Recommender buildRecommender(
			RecommenderParameters recommenderParameters,
			ParametersModel parametersModel) {
		System.out.println(">>>>>> "+recommenderParameters.getType());
		if (recommenderParameters.getType().equals(
				Recommender.WORKFLOWS_COLLABORATIVE_FILTER)) {

			return new WorkflowsCollaborativeFilterRecommender(
					recommenderParameters);
		} else if (recommenderParameters.getType().equals(
				Recommender.FILES_COLLABORATIVE_FILTER)) {
			return new FilesCollaborativeFilterRecommender(
					recommenderParameters);

		} else if (recommenderParameters.getType().equals(
				Recommender.KEYWORD_CONTENT_BASED)) {
			return new WorkflowsKeywordContentBasedRecommender(
					recommenderParameters, parametersModel);
		} else if (recommenderParameters.getType().equals(
				Recommender.USERS_SOCIAL_NETWORK)) {
			return new UsersSocialNetworkRecommender(recommenderParameters,
					parametersModel);
		} else if (recommenderParameters.getType().equals(
				Recommender.WORKFLOWS_GROUP_BASED)) {
			return new WorkflowsGroupBasedRecommender(recommenderParameters,
					parametersModel);
		}
		return null;
	}
}