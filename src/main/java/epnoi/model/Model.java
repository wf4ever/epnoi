package epnoi.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "model", namespace = "http://www.wf4ever-project.org/wp3/recommendersystem")
public class Model {
	private ArrayList<User> users;
	private ArrayList<Workflow> workflows;
	private ArrayList<File> files;
	private ArrayList<Rating> ratings;
	private ArrayList<Group> groups;
	private ArrayList<Pack> packs;

	private HashMap<String, HashMap<String, ArrayList<Rating>>> ratingsByUser;
	private HashMap<String, Workflow> workflowsByURI;
	private HashMap<String, User> usersByURI;
	private HashMap<Long, User> userByID;
	private HashMap<String, File> filesByURI;
	private HashMap<String, Pack> packsByURI;

	private HashMap<Long, Workflow> workflowsByID;
	private HashMap<Long, File> filesByID;
	private HashMap<Long, Pack> packsByID;

	public Model() {
		this.users = new ArrayList<User>();
		this.workflows = new ArrayList<Workflow>();
		this.files = new ArrayList<File>();
		this.ratings = new ArrayList<Rating>();
		this.groups = new ArrayList<Group>();
		this.ratingsByUser = new HashMap<String, HashMap<String, ArrayList<Rating>>>();
		this.workflowsByURI = new HashMap<String, Workflow>();
		this.usersByURI = new HashMap<String, User>();
		this.userByID = new HashMap<Long, User>();
		this.workflowsByID = new HashMap<Long, Workflow>();
		this.filesByID = new HashMap<Long, File>();
		this.filesByURI = new HashMap<String, File>();
		this.packs = new ArrayList<Pack>();
		this.packsByID = new HashMap<Long, Pack>();
		this.packsByURI = new HashMap<String, Pack>();
	}

	public void init() {
		// Initialization of the rating by user data structure

		this.ratingsByUser = new HashMap<String, HashMap<String, ArrayList<Rating>>>();

		HashMap<String, ArrayList<Rating>> workflowsRatingsByUser = new HashMap<String, ArrayList<Rating>>();
		HashMap<String, ArrayList<Rating>> filesRatingsByUser = new HashMap<String, ArrayList<Rating>>();

		this.ratingsByUser.put(Rating.WORKFLOW_RATING, workflowsRatingsByUser);
		this.ratingsByUser.put(Rating.FILE_RATING, filesRatingsByUser);

		for (User user : this.users) {

			workflowsRatingsByUser.put(user.getURI(), new ArrayList<Rating>());
			filesRatingsByUser.put(user.getURI(), new ArrayList<Rating>());
		}

		for (Rating rating : this.ratings) {
			if (rating.getType().equals(Rating.WORKFLOW_RATING)) {

				ArrayList<Rating> userRatings = workflowsRatingsByUser
						.get(rating.getOwnerURI());
				userRatings.add(rating);
			}
			if (rating.getType().equals(Rating.FILE_RATING)) {
				ArrayList<Rating> userRatings = filesRatingsByUser.get(rating
						.getOwnerURI());
				userRatings.add(rating);
			}

		}

		for (User user : this.users) {
			this.usersByURI.put(user.getURI(), user);
			this.userByID.put(user.getID(), user);
		}

		for (Workflow workflow : this.workflows) {
			this.workflowsByURI.put(workflow.getURI(), workflow);
			this.workflowsByID.put(workflow.getID(), workflow);
		}

		for (File file : this.files) {
			this.filesByURI.put(file.getURI(), file);
			this.filesByID.put(file.getID(), file);
		}

		for (Pack pack : this.packs) {
			this.packsByURI.put(pack.getURI(), pack);
			this.packsByID.put(pack.getID(), pack);
		}

	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public ArrayList<Rating> getRatings() {
		return ratings;
	}

	public void setRatings(ArrayList<Rating> ratings) {
		this.ratings = ratings;
	}

	public ArrayList<Workflow> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(ArrayList<Workflow> workflows) {
		this.workflows = workflows;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}

	public ArrayList<Group> getGroups() {
		return groups;
	}

	public ArrayList<Rating> getRatingsByUser(String userURI, String ratingType) {

		return this.ratingsByUser.get(ratingType).get(userURI);

	}

	public User getUserByURI(String userURI) {
		return this.usersByURI.get(userURI);
	}
	
	public User getUserByID(Long userID){
		return this.userByID.get(userID);
	}

	public Workflow getWorkflowByURI(String workflowURI) {

		return this.workflowsByURI.get(workflowURI);
	}

	public Workflow getWorkflowByID(Long id) {
		return this.workflowsByID.get(id);
	}

	public File getFileByURI(String fileURI) {
		return this.filesByURI.get(fileURI);
	}

	public File getFileByID(Long id) {
		return this.filesByID.get(id);
	}

	public ArrayList<Pack> getPacks() {
		return packs;
	}

	public void setPacks(ArrayList<Pack> packs) {
		this.packs = packs;
	}

	public Pack getPackByURI(String packURI) {
		return this.packsByURI.get(packURI);
	}

	public Pack getPackByID(Long id) {
		return this.packsByID.get(id);
	}

	public boolean isWorkflow(String URI) {
		return this.workflowsByURI.containsKey(URI);
	}

	public boolean isPack(String URI) {
		return this.packsByURI.containsKey(URI);
	}

	public boolean isFile(String URI) {
		return this.filesByURI.containsKey(URI);
	}

	public HashMap<String, Workflow> getWorkflowsByURI() {
		return workflowsByURI;
	}

	public void setWorkflowsByURI(HashMap<String, Workflow> workflowsByURI) {
		this.workflowsByURI = workflowsByURI;
	}

	public HashMap<Long, Workflow> getWorkflowsByID() {
		return workflowsByID;
	}

	public void setWorkflowsByID(HashMap<Long, Workflow> workflowsByID) {
		this.workflowsByID = workflowsByID;
	}

	public void addWorkflow(Workflow workflow) {
		this.workflows.add(workflow);
		this.workflowsByURI.put(workflow.getURI(), workflow);
		this.workflowsByID.put(workflow.getID(), workflow);
	}

	public void addFile(File file) {
		this.files.add(file);
		this.filesByURI.put(file.getURI(), file);
		this.filesByID.put(file.getID(), file);
	}

	public void addPack(Pack file) {
		this.packs.add(file);
		this.packsByURI.put(file.getURI(), file);
		this.packsByID.put(file.getID(), file);
	}
}
