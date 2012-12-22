package epnoi.model;

public class ExternalResource implements Resource {

	private String URI;
	private String description;

	// ------------------------------------------------------------------------------------------

	public String getURI() {
		return URI;
	}

	// ------------------------------------------------------------------------------------------
	
	public void setURI(String uRI) {
		URI = uRI;
	}
	
	// ------------------------------------------------------------------------------------------

	public String getDescription() {
		return description;
	}
	
	// ------------------------------------------------------------------------------------------

	public void setDescription(String description) {
		this.description = description;
	}

	// ------------------------------------------------------------------------------------------
	
	public String toString() {
		return "ER[URI: " + this.URI + " , description: " + this.description
				+ "]";
	}

}
