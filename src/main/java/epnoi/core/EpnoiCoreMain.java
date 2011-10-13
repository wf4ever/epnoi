package epnoi.core;

import java.util.ArrayList;

import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.User;

public class EpnoiCoreMain {

	public static void main(String[] args) {
		System.out.println("Starting the EpnoiCoreMain");
		EpnoiCore epnoiCore = new EpnoiCore();
		epnoiCore.init("/model.xml");
		ArrayList<String> differentRaters = new ArrayList<String>();
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (!differentRaters.contains(rating.getOwnerURI())
					&& rating.WORKFLOW_RATING.equals(rating.getType())) {
				differentRaters.add(rating.getOwnerURI());
			}
		}

		System.out.println("# of recommendations"
				+ epnoiCore.getRecommendationSpace().getAllRecommendations()
						.size());

		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			System.out.println("Recommendation for "
					+ recommendation.getUserURI());

			System.out.println("         (i)" + recommendation.getItemID());
			System.out.println("         (s)" + recommendation.getStrength());
		}

		System.out.println("# of users> "
				+ epnoiCore.getModel().getUsers().size());
		System.out.println("# of workflows> "
				+ epnoiCore.getModel().getWorflows().size());
		System.out.println("# of files> "
				+ epnoiCore.getModel().getFiles().size());
		System.out.println("# of users with at least one rating"
				+ differentRaters.size());

		ArrayList<String> differentUploaders = new ArrayList<String>();
		ArrayList<String> differentFavouriters = new ArrayList<String>();
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getWorkflows().size() > 0) {
				differentUploaders.add(user.getURI());
			}
			if (user.getFavouritedWorkflows().size() > 0) {
				differentFavouriters.add(user.getURI());
			}

		}
		System.out.println("# of users that have uploaded a workflow"
				+ differentUploaders.size());
		System.out.println("# of users with at least one favourite"
				+ differentFavouriters.size());
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
			if (!differentRecommendedUsers.contains(recommendation.getUserURI())) {
				differentRecommendedUsers.add(recommendation.getUserURI());
			}
			if (!differentRatedWorkflow.contains(recommendation.getItemID())) {
				differentRatedWorkflow.add(recommendation.getItemID());
			}

		}
		System.out.println("# of users that have received a recommendation"
				+ differentRecommendedUsers.size());
		System.out.println("# of workflows that have been recommended"
				+differentRatedWorkflow.size());

	}

}
