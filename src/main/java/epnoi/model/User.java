package epnoi.model;

import java.util.ArrayList;

public class User {

	Long ID;
	String URI;
	String resource;
	String description;
	String name;

	ArrayList<String> tagApplied;
	ArrayList<String> friends;
	ArrayList<String> favouritedWorkflows;
	ArrayList<String> favouritedFiles;
	ArrayList<String> groups;
	ArrayList<String> files;
	ArrayList<String> workflows;

	public User() {
		this.tagApplied = new ArrayList<String>();
		this.favouritedWorkflows = new ArrayList<String>();
		this.favouritedFiles = new ArrayList<String>();
		this.groups = new ArrayList<String>();
		this.files = new ArrayList<String>();
		this.workflows = new ArrayList<String>();
		this.friends = new ArrayList<String>();
	}

	public ArrayList<String> getFavouritedFiles() {
		return favouritedFiles;
	}

	public void setFavouritedFiles(ArrayList<String> favouritedFiles) {
		this.favouritedFiles = favouritedFiles;
	}

	public ArrayList<String> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(ArrayList<String> workflows) {
		this.workflows = workflows;
	}

	public ArrayList<String> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<String> friends) {
		this.friends = friends;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getFavouritedWorkflows() {
		return favouritedWorkflows;
	}

	public void setFavouritedWorkflows(ArrayList<String> favouritedWorkflows) {
		this.favouritedWorkflows = favouritedWorkflows;
	}

	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
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

	public ArrayList<String> getTagApplied() {
		return tagApplied;
	}

	public void setTagApplied(ArrayList<String> tagApplied) {
		this.tagApplied = tagApplied;
	}

	public ArrayList<String> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<String> files) {
		this.files = files;
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
