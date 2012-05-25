package epnoi.model.parameterization;

public class SocialNetworkRecommenderParameters extends RecommenderParameters {
	private Integer numberOfRecommendations;
	private String databasePath;

	public String getDatabasePath() {
		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	public Integer getNumberOfRecommendations() {
		return numberOfRecommendations;
	}

	public void setNumberOfRecommendations(Integer numberOfRecommendations) {
		this.numberOfRecommendations = numberOfRecommendations;
	}
}
