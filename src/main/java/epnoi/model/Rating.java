package epnoi.model;

public class Rating {
	public static final String WORKFLOW_RATING ="WORKFLOW";
	public static final String FILE_RATING ="FILE";
	
	String URI;
	Long ID;
	String ownerResource;
	String ownerURI;
	String ratedElement;
	Long ratedElementID;
	Integer ratingValue;
	String resource;
	String type;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getOwnerResource() {
		return ownerResource;
	}

	public void setOwnerResource(String ownerResource) {
		this.ownerResource = ownerResource;
	}

	public String getOwnerURI() {
		return ownerURI;
	}

	public void setOwnerURI(String ownerURI) {
		this.ownerURI = ownerURI;
	}

	public String getRatedElement() {
		return ratedElement;
	}

	public void setRatedElement(String ratedElement) {
		this.ratedElement = ratedElement;
	}

	public Long getRatedElementID() {
		return ratedElementID;
	}

	public void setRatedElementID(Long ratedElementID) {
		this.ratedElementID = ratedElementID;
	}

	public Integer getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(Integer ratingValue) {
		this.ratingValue = ratingValue;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

}
