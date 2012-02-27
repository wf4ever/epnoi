package epnoi.model;

import java.util.ArrayList;

public class Pack implements Resource{
	Long ID;
	String URI;
	String resource;
	String title;
	String description;
	ArrayList<String> internalWorkflows;
	ArrayList<String> internalFiles;

	public Pack() {
		this.internalWorkflows = new ArrayList<String>();
		this.internalFiles = new ArrayList<String>();
	}

	public ArrayList<String> getInternalWorkflows() {
		return internalWorkflows;
	}

	public void setInternalWorkflows(ArrayList<String> internalWorkflows) {
		this.internalWorkflows = internalWorkflows;
	}

	public ArrayList<String> getInternalFiles() {
		return internalFiles;
	}

	public void setInternalFiles(ArrayList<String> internalFiles) {
		this.internalFiles = internalFiles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long id) {
		this.ID = id;
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

}
