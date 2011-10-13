package epnoi.model;

import java.util.ArrayList;

public class Workflow {

	Long id;
	String URI;
	String resource;
	String description;
	String title;
	String contentType;
	String contentURI;
	String uploaderURI;
	ArrayList<String> tags;

	public Workflow() {
		this.tags = new ArrayList<String>();
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentURI() {
		return contentURI;
	}

	public void setContentURI(String contentURI) {
		this.contentURI = contentURI;
	}

	public String getUploaderURI() {
		return uploaderURI;
	}

	public void setUploaderURI(String uploaderURI) {
		this.uploaderURI = uploaderURI;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
