package epnoi.model;

import java.util.ArrayList;

public class Group {
	boolean autoAccept;
	String description;
	Long id;
	String owner;
	String resource;
	String title;
	String URI;
	ArrayList<String> tags;
	
	Group(){
		this.tags = new ArrayList<String>();
	}

	public boolean isAutoAccept() {
		return autoAccept;
	}

	public void setAutoAccept(boolean autoAccept) {
		this.autoAccept = autoAccept;
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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

	public String getURI() {
		return this.URI;
	}

	public void setURI(String URI) {
		this.URI = URI;
	}

}
