package epnoi.recommeders;

import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.Workflow;

public class FilesCollaborativeFilterRecommender implements
		CollaborativeFilterRecommender {
	// In the near future this constants should be parameters
	int NEIGHBORHODD_SIZE = 5;
	int NUMBER_OF_RECOMMENDATION = 5;

	Model model = null;
	UserSimilarity similarity = null;
	UserNeighborhood neighborhood = null;
	Recommender recommender = null;
	DataModel dataModel = null;

	public void init(Model model) {
		this.model = model;
		System.out.println("(model)> " + model);
		_initData();

		try {
			similarity = new EuclideanDistanceSimilarity(dataModel);

			neighborhood = new NearestNUserNeighborhood(this.NEIGHBORHODD_SIZE,
					similarity, dataModel);

			recommender = new GenericUserBasedRecommender(dataModel,
					neighborhood, similarity);
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void recommend(RecommendationSpace recommedationSpace) {

		try {
			for (User user : this.model.getUsers()) {
				List<RecommendedItem> recommendations = recommender.recommend(
						user.getID(), NUMBER_OF_RECOMMENDATION);

				for (RecommendedItem recommendedItem : recommendations) {
					Recommendation recommendation = new Recommendation();
					recommendation.setUserURI(user.getURI());
					recommendation.setStrength(recommendedItem.getValue());
					recommendation.setItemID(recommendedItem.getItemID());
					File file = this.model.getFileByID(recommendation
							.getItemID());
					recommendation.setItemURI(file.getUri());
					recommedationSpace.addRecommendationForUser(user,
							recommendation);
				}

			}

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void _initData() {
		FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();

		for (User user : this.model.getUsers()) {

			PreferenceArray pref = this._getUserPreferences(user);
			preferences.put(user.getID(), pref);
		}
		this.dataModel = new GenericDataModel(preferences);
	}

	private PreferenceArray _getUserPreferences(User user) {

		int numberOfFavouritedFiles = user.getFavouritedFiles().size();

		int numberOfRatedFiles = model.getRatingsByUser(user.getURI(),
				Rating.FILE_RATING).size();
		int numberOfOwnedWorkflows = user.getFiles().size();

		PreferenceArray userPreferences = new GenericUserPreferenceArray(
				numberOfFavouritedFiles + numberOfRatedFiles
						+ numberOfOwnedWorkflows);

		userPreferences.setUserID(0, user.getID());
		int ratingIndex = 0;

		for (Rating rating : model.getRatingsByUser(user.getURI(),
				Rating.FILE_RATING)) {

			userPreferences.setItemID(ratingIndex, rating.getRatedElementID());

			userPreferences.setValue(ratingIndex, rating.getRatingValue());
			ratingIndex++;
		}

		for (String fileURI : user.getFavouritedFiles()) {
			File file = this.model.getFileByURI(fileURI);
			userPreferences.setItemID(ratingIndex, file.getId());

			userPreferences.setValue(ratingIndex, 5);
			ratingIndex++;
		}

		for (String fileURI : user.getFiles()) {
			File file = this.model.getFileByURI(fileURI);
			userPreferences.setItemID(ratingIndex, file.getId());
			userPreferences.setValue(ratingIndex, 5);
			ratingIndex++;
		}

		return userPreferences;
	}

	public void close() {

	}

}
