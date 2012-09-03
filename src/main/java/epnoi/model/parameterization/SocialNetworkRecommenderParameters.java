package epnoi.model.parameterization;

public class SocialNetworkRecommenderParameters extends RecommenderParameters {
	private Integer numberOfRecommendations;

	public Integer getNumberOfRecommendations() {
		return numberOfRecommendations;
	}

	public void setNumberOfRecommendations(Integer numberOfRecommendations) {
		this.numberOfRecommendations = numberOfRecommendations;
	}
	
	@Override
	public String toString(){
		return "Social Recommender[#Recommendations:"+this.numberOfRecommendations+"]";
	}
}
