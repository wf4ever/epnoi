package epnoi.model;

public class File implements Resource{
	Long ID;
	String URI;
	String resource;
	String title;

	public Long getID() {
		return ID;
	}

	public void setID(Long id) {
		this.ID = id;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uri) {
		this.URI = uri;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

	
}
