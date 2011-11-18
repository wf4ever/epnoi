package epnoi.core;

import java.util.ArrayList;
import java.util.Collections;

import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.Tagging;
import epnoi.model.User;

public class EpnoiCoreMain {
	public static final String MODEL_PATH = "/lastImportedModel.xml";

	// public static final String MODEL_PATH = "/model.xml";

	public static void main(String[] args) {
		System.out.println("Starting the EpnoiCoreMain");
		EpnoiCore epnoiCore = new EpnoiCore();
		epnoiCore.init(MODEL_PATH);
		ArrayList<String> differentRaters = new ArrayList<String>();
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (!differentRaters.contains(rating.getOwnerURI())
					&& rating.WORKFLOW_RATING.equals(rating.getType())) {
				differentRaters.add(rating.getOwnerURI());
			}
		}

		;

		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			System.out.println("Recommendation for "
					+ recommendation.getUserURI());

			System.out.println("         (i)" + recommendation.getItemID());
			System.out.println("         (s)" + recommendation.getStrength());
			System.out
					.println("         (i.URI)" + recommendation.getItemURI());

		}
		System.out.println("# of recommendations"
				+ epnoiCore.getRecommendationSpace().getAllRecommendations()
						.size());
		System.out.println("# of users> "
				+ epnoiCore.getModel().getUsers().size());
		System.out.println("# of workflows> "
				+ epnoiCore.getModel().getWorflows().size());
		System.out.println("# of files> "
				+ epnoiCore.getModel().getFiles().size());
		System.out.println("# of users with at least one rating"
				+ differentRaters.size());

		int numberOfFavouritedWorkflows = 0;
		ArrayList<String> differentUploaders = new ArrayList<String>();
		ArrayList<String> differentFavouriters = new ArrayList<String>();
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getWorkflows().size() > 0) {
				differentUploaders.add(user.getURI());
			}
			if (user.getFavouritedWorkflows().size() > 0) {
				differentFavouriters.add(user.getURI());
				numberOfFavouritedWorkflows += user.getFavouritedWorkflows()
						.size();
			}

		}
		System.out.println("# of users that have uploaded a workflow"
				+ differentUploaders.size());
		System.out.println("# of users with at least one favourite workflow"
				+ differentFavouriters.size());
		System.out.println("# of favourited workflows (they may be repeated)"
				+ numberOfFavouritedWorkflows);

		ArrayList<String> differentFavouritersAndRaters = new ArrayList<String>();
		for (String userURI : differentFavouriters) {

			if (differentRaters.contains(userURI)) {
				differentFavouritersAndRaters.add(userURI);
			}
		}
		System.out.println("# of users with at least one favourite and rating"
				+ differentFavouritersAndRaters.size());

		ArrayList<String> differentRecommendedUsers = new ArrayList<String>();
		ArrayList<Long> differentRatedWorkflow = new ArrayList<Long>();

		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			if (!differentRecommendedUsers
					.contains(recommendation.getUserURI())) {
				differentRecommendedUsers.add(recommendation.getUserURI());
			}
			if (!differentRatedWorkflow.contains(recommendation.getItemID())) {
				differentRatedWorkflow.add(recommendation.getItemID());
			}

		}
		System.out.println("# of users that have received a recommendation"
				+ differentRecommendedUsers.size());
		System.out.println("# of workflows that have been rated"
				+ differentRatedWorkflow.size());

		int numberOfFavouritedFiles = 0;
		ArrayList<String> differentFilesUploaders = new ArrayList<String>();
		ArrayList<String> differentFilesFavouriters = new ArrayList<String>();
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getFiles().size() > 0) {
				differentFilesUploaders.add(user.getURI());
			}
			if (user.getFavouritedFiles().size() > 0) {
				differentFilesFavouriters.add(user.getURI());
				numberOfFavouritedFiles += user.getFavouritedFiles().size();
			}

		}
		System.out.println("# of users that have uploaded a file"
				+ differentFilesUploaders.size());
		System.out.println("# of users with at least one favourite file"
				+ differentFilesFavouriters.size());
		System.out.println("# of favourited files  (they may be repeated)"
				+ numberOfFavouritedFiles);

		int ratingsForFiles = 0;
		int ratingsForWorkflows = 0;
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (rating.getType().equals(Rating.FILE_RATING)) {
				ratingsForFiles++;
			}
			if (rating.getType().equals(Rating.WORKFLOW_RATING)) {
				ratingsForWorkflows++;
			}
		}

		System.out.println("# of file ratings" + ratingsForFiles);
		System.out.println("# of workflow ratings" + ratingsForWorkflows);

		int numberOfUsersWithTags = 0;
		float averageNumberOfTags = 0;
		int numberOfTags = 0;
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getTagApplied().size() > 0) {

				numberOfUsersWithTags++;
				numberOfTags += user.getTagApplied().size();
			}
		}
		System.out.println("# of tags" + numberOfTags);
		System.out.println("# of users with at least one tag"
				+ numberOfUsersWithTags);
		System.out.println("# of the average tag per user "
				+ ((float) numberOfTags)
				/ ((float) epnoiCore.getModel().getUsers().size()));
		System.out.println("# of the average tag per user that has tags"
				+ ((float) numberOfTags) / ((float) numberOfUsersWithTags));

	}

	public ArrayList<Tagging> orderByFrequency(ArrayList<Tagging> taggingsList) {
		ArrayList<Tagging> taggingsListOrdered = (ArrayList<Tagging>) taggingsList
				.clone();
		Collections.sort(taggingsListOrdered);
		return taggingsListOrdered;

	}

}
