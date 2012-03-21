package epnoi.model.parameterization;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "collaborativeFilterRecommender")
public class CollaborativeFilterRecommenderParameters extends
		RecommenderParameters {

	
	private String similarity;
	private String neighbourhoodType;
	private Integer neighbourhoohdSize;
	private Float neighbourhoodThreshold;


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

	
}
