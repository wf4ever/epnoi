package epnoi.model.parameterization;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "collaborativeFilterRecommender")
public class CollaborativeFilterRecommenderParameters extends
		RecommenderParameters {

	
	private String similarity;
	private String neighbourhoodType;
	private Integer neighbourhoohdSize;
	private Float neighbourhoodThreshold;
	private Integer numberOfRecommendations;


	public String getSimilarity() {
		return similarity;
	}

	public  void setSimilarity(String similarity) {
		this.similarity = similarity;
	}

	public  String getNeighbourhoodType() {
		return neighbourhoodType;
	}

	public  void setNeighbourhoodType(String neighbourhoodType) {
		this.neighbourhoodType = neighbourhoodType;
	}

	public  Integer getNeighbourhoohdSize() {
		return neighbourhoohdSize;
	}

	public void setNeighbourhoohdSize(Integer neighbourhoohdSize) {
		this.neighbourhoohdSize = neighbourhoohdSize;
	}

	public Float getNeighbourhoodThreshold() {
		return neighbourhoodThreshold;
	}

	public void setNeighbourhoodThreshold(Float neighbourhoodThreshold) {
		this.neighbourhoodThreshold = neighbourhoodThreshold;
	}

	public Integer getNumberOfRecommendations() {
		return numberOfRecommendations;
	}

	public void setNumberOfRecommendations(Integer numberOfRecommendations) {
		this.numberOfRecommendations = numberOfRecommendations;
	}

	@Override
	public String toString(){
		return "Collaborative Filter Recommender[ItemType:"+super.getType()+", #Recommendations:"+this.numberOfRecommendations+", NeighbourhoodType: "+this.neighbourhoodType+", NeighbourhoodSize: "+this.neighbourhoohdSize +" , NeighbourhoodThreshold:"+this.neighbourhoodThreshold+"]";
	}
	
}
