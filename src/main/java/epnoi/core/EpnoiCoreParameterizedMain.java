package epnoi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelWrapper;
import epnoi.recommeders.Recommender;

public class EpnoiCoreParameterizedMain {

	public static void main(String[] args) {
		System.out.println("Starting the EpnoiCoreMain");
		EpnoiCore epnoiCore = new EpnoiCore();

		ParametersModel parametersModel = ParametersModelWrapper
				.read("/parametersModelPath.xml");

		/*
		 * Properties initializationProperties = new Properties();
		 * 
		 * initializationProperties.setProperty(EpnoiCore.INDEX_PATH_PROPERTY,
		 * "/wf4ever/indexMyExperiment");
		 * initializationProperties.setProperty(EpnoiCore.MODEL_PATH_PROPERTY,
		 * "/wf4ever/lastImportedModel.xml");
		 */
		epnoiCore.init(parametersModel);
		ArrayList<String> differentRaters = new ArrayList<String>();
		for (Rating rating : epnoiCore.getModel().getRatings()) {
			if (!differentRaters.contains(rating.getOwnerURI())){
				differentRaters.add(rating.getOwnerURI());
			}
		}

		/*
		 * for (Recommendation recommendation :
		 * epnoiCore.getRecommendationSpace() .getAllRecommendations()) {
		 * System.out.println("Recommendation for " +
		 * recommendation.getUserURI());
		 * 
		 * System.out.println("         (i)" + recommendation.getItemID());
		 * System.out.println("         (s)" + recommendation.getStrength());
		 * System.out .println("         (i.URI)" +
		 * recommendation.getItemURI());
		 * 
		 * }
		 */
		System.out.println("# of recommendations "
				+ epnoiCore.getRecommendationSpace().getAllRecommendations()
						.size());
		System.out.println("# of users> "
				+ epnoiCore.getModel().getUsers().size());
		System.out.println("# of workflows> "
				+ epnoiCore.getModel().getWorkflows().size());
		System.out.println("# of files> "
				+ epnoiCore.getModel().getFiles().size());
		System.out.println("# of users with at least one rating "
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
		System.out.println("# of users that have uploaded a workflow "
				+ differentUploaders.size());
		System.out.println("# of users with at least one favourite workflow "
				+ differentFavouriters.size());
		System.out.println("# of favourited workflows (they may be repeated) "
				+ numberOfFavouritedWorkflows);

		ArrayList<String> differentFavouritersAndRaters = new ArrayList<String>();
		for (String userURI : differentFavouriters) {

			if (differentRaters.contains(userURI)) {
				differentFavouritersAndRaters.add(userURI);
			}
		}
		System.out.println("# of users with at least one favourite and rating "
				+ differentFavouritersAndRaters.size());

		ArrayList<String> differentRecommendedUsers = new ArrayList<String>();
		ArrayList<Long> differentRatedWorkflow = new ArrayList<Long>();
		int contentBased = 0;
		int collaborativeBased = 0;
		int socialbased = 0;

		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			if (!differentRecommendedUsers
					.contains(recommendation.getUserURI())) {
				differentRecommendedUsers.add(recommendation.getUserURI());
			}
			if (!differentRatedWorkflow.contains(recommendation.getItemID())) {
				differentRatedWorkflow.add(recommendation.getItemID());
			}

			if (recommendation.getProvenance().getParameterByName("technique")
					.equals("collaborative-filtering")) {
				collaborativeBased++;
			} else {
				
				if (recommendation.getProvenance().getParameterByName("technique")
						.equals("social-based")) {
					socialbased++;
				} else {
					contentBased++;	
				}
				
				
			}
		}

		System.out.println("# of users that have received a recommendation "
				+ differentRecommendedUsers.size());
		System.out.println("# of items that have been recommended "
				+ differentRatedWorkflow.size());

		System.out
				.println("# of recommendations by collaborative filtering algorithm "
						+ collaborativeBased);
		System.out.println("# of recommendations by content based algorithm "
				+ contentBased);

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
		System.out.println("# of users that have uploaded a file "
				+ differentFilesUploaders.size());
		System.out.println("# of users with at least one favourite file "
				+ differentFilesFavouriters.size());
		System.out.println("# of favourited files  (they may be repeated) "
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

		System.out.println("# of file ratings " + ratingsForFiles);
		System.out.println("# of workflow ratings " + ratingsForWorkflows);

		int numberOfUsersWithTags = 0;
		float averageNumberOfTags = 0;
		int numberOfTags = 0;
		for (User user : epnoiCore.getModel().getUsers()) {
			if (user.getTagApplied().size() > 0) {

				numberOfUsersWithTags++;
				numberOfTags += user.getTagApplied().size();
			}
		}
		System.out.println("# of tags " + numberOfTags);
		System.out.println("# of users with at least one tag "
				+ numberOfUsersWithTags);
		System.out.println("# of the average tag per user "
				+ ((float) numberOfTags)
				/ ((float) epnoiCore.getModel().getUsers().size()));
		System.out.println("# of the average tag per user that has tags "
				+ ((float) numberOfTags) / ((float) numberOfUsersWithTags));

		System.out.println("# of packs "
				+ epnoiCore.getModel().getPacks().size());
/*
		for (Recommendation recommendation : epnoiCore.getRecommendationSpace()
				.getAllRecommendations()) {
			System.out.println(">>>>> " + recommendation);
		}
*/
		epnoiCore.close();
	}

	public ArrayList<Tagging> orderByFrequency(ArrayList<Tagging> taggingsList) {
		ArrayList<Tagging> taggingsListOrdered = (ArrayList<Tagging>) taggingsList
				.clone();
		Collections.sort(taggingsListOrdered);
		return taggingsListOrdered;

	}

}
