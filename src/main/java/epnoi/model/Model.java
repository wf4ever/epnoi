package epnoi.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "model", namespace = "http://www.wf4ever-project.org/wp3/recommendersystem")
public class Model {
	private ArrayList<User> users;
	private ArrayList<Workflow> worflows;
	private ArrayList<File> files;
	private ArrayList<Rating> ratings;
	private ArrayList<Group> groups;

	private HashMap<String, HashMap<String, ArrayList<Rating>>> ratingsByUser;
	private HashMap<String, Workflow> workflowsByURI;
	private HashMap<String, User> usersByURI;
	private HashMap<String, File> filesByURI;

	private HashMap<Long, Workflow> workflowsByID;
	private HashMap<Long, File> filesByID;

	public Model() {
		this.users = new ArrayList<User>();
		this.worflows = new ArrayList<Workflow>();
		this.files = new ArrayList<File>();
		this.ratings = new ArrayList<Rating>();
		this.groups = new ArrayList<Group>();
		this.ratingsByUser = new HashMap<String, HashMap<String, ArrayList<Rating>>>();
		this.workflowsByURI = new HashMap<String, Workflow>();
		this.usersByURI = new HashMap<String, User>();
		this.workflowsByID = new HashMap<Long, Workflow>();
		this.filesByID = new HashMap<Long, File>();
		this.filesByURI = new HashMap<String, File>();
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
		}

		for (Workflow workflow : this.worflows) {
			this.workflowsByURI.put(workflow.getURI(), workflow);
			this.workflowsByID.put(workflow.getId(), workflow);
		}

		for (File file : this.files) {
			this.filesByURI.put(file.getUri(), file);
			this.filesByID.put(file.getId(), file);
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

	public ArrayList<Workflow> getWorflows() {
		return worflows;
	}

	public void setWorflows(ArrayList<Workflow> workflows) {
		this.worflows = workflows;
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


}
