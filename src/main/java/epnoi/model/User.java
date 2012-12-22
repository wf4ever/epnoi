package epnoi.model;

import java.util.ArrayList;
import java.util.Iterator;

public class User implements Resource {

	Long ID;
	String URI;
	String resource;
	String description;
	String name;

	ArrayList<Tagging> tagApplied;
	ArrayList<String> friends;
	ArrayList<String> favouritedWorkflows;
	ArrayList<String> favouritedFiles;
	ArrayList<String> groups;
	ArrayList<String> files;
	ArrayList<String> workflows;
	ArrayList<String> packs;
	ArrayList<Action> actions;

	public User() {
		this.tagApplied = new ArrayList<Tagging>();
		this.favouritedWorkflows = new ArrayList<String>();
		this.favouritedFiles = new ArrayList<String>();
		this.groups = new ArrayList<String>();
		this.files = new ArrayList<String>();
		this.workflows = new ArrayList<String>();
		this.friends = new ArrayList<String>();
		this.packs = new ArrayList<String>();
		this.actions = new ArrayList<Action>();
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

	public ArrayList<Tagging> getTagApplied() {
		return tagApplied;
	}

	public void setTagApplied(ArrayList<Tagging> tagApplied) {
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

	public void addTagging(String tag) {
		// System.out.println(">> entra " + tag);
		Iterator<Tagging> taggingIterator = this.tagApplied.iterator();
		boolean finded = false;
		while (!finded && taggingIterator.hasNext()) {
			Tagging tagging = taggingIterator.next();
			finded = tagging.getTag().equals(tag);
			if (finded) {
				// System.out.println(">> a–ado +1 " + tag);
				tagging.setNumberOfTaggings(tagging.getNumberOfTaggings() + 1);
			}
		}
		if (!finded) {
			// System.out.println(">> creo " + tag + " en " + this.ID);
			Tagging newTagging = new Tagging();
			newTagging.setTag(tag);
			newTagging.setNumberOfTaggings(1);
			this.tagApplied.add(newTagging);

		}
	}

	public ArrayList<String> getPacks() {
		return packs;
	}

	public void setPacks(ArrayList<String> packs) {
		this.packs = packs;
	}

}
