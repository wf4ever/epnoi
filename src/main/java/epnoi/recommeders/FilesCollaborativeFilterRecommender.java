package epnoi.recommeders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import epnoi.model.Explanation;
import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.parameterization.CollaborativeFilterRecommenderParameters;
import epnoi.model.parameterization.RecommenderParameters;

public class FilesCollaborativeFilterRecommender implements
		CollaborativeFilterRecommender {
	// In the near future this constants should be parameters

	int NUMBER_OF_RECOMMENDATION = 5;

	Model model = null;
	UserSimilarity similarity = null;
	UserNeighborhood neighborhood = null;
	Recommender recommender = null;
	DataModel dataModel = null;
	CollaborativeFilterRecommenderParameters recommenderParameters = null;

	public FilesCollaborativeFilterRecommender(
			RecommenderParameters recommenderParameters) {
		this.recommenderParameters = (CollaborativeFilterRecommenderParameters) recommenderParameters;
	}

	public void init(Model model) {

		this.model = model;
		this.recommenderParameters = (CollaborativeFilterRecommenderParameters) recommenderParameters;
		this._initData();

		try {
			if (epnoi.recommeders.Recommender.SIMILARITY_EUCLIDEAN
					.equals(this.recommenderParameters.getSimilarity())) {

				this.similarity = new EuclideanDistanceSimilarity(dataModel);

			} else if (epnoi.recommeders.Recommender.SIMILARITY_PEARSON_CORRELATION
					.equals(this.recommenderParameters.getSimilarity())) {
				this.similarity = new PearsonCorrelationSimilarity(dataModel);
			}

			if (epnoi.recommeders.Recommender.NEIGHBOURHOOD_TYPE_NEAREST
					.equals(this.recommenderParameters.getNeighbourhoodType())) {
				Integer size = this.recommenderParameters
						.getNeighbourhoohdSize();
				neighborhood = new NearestNUserNeighborhood(size, similarity,
						dataModel);
			} else if (epnoi.recommeders.Recommender.NEIGHBOURHOOD_TYPE_THRESHOLD
					.equals(this.recommenderParameters.getNeighbourhoodType())) {
				Float threshold = this.recommenderParameters
						.getNeighbourhoodThreshold();
				neighborhood = new ThresholdUserNeighborhood(threshold,
						similarity, dataModel);
			}
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
					recommendation.setItemURI(file.getURI());
					
					
					ArrayList<UserSimilarityValue> similarUsers = new ArrayList<UserSimilarityValue>();
					for (Long neighbourhoodUserID : neighborhood
							.getUserNeighborhood(user.getID())) {
						User neighbourhoodUser = this.model
								.getUserByID(neighbourhoodUserID);
						UserSimilarityValue userSimilarityValue = new UserSimilarityValue();
						userSimilarityValue.setUserURI(neighbourhoodUser
								.getName());
						userSimilarityValue.setSimilarity(this.similarity
								.userSimilarity(user.getID(),
										neighbourhoodUser.getID()));
						similarUsers.add(userSimilarityValue);

					}
					Collections.sort(similarUsers);
					Collections.reverse(similarUsers);
					String similarUsersNames = "";
					Iterator<UserSimilarityValue> similarUsersIt = similarUsers
							.iterator();
					while (similarUsersIt.hasNext()) {
						UserSimilarityValue userSimilarityValue = similarUsersIt
								.next();
						if (similarUsersIt.hasNext()) {
							similarUsersNames += (" "
									+ userSimilarityValue.getUserURI() + ", ");
						} else {
							similarUsersNames += userSimilarityValue
									.getUserURI();

						}
					}

					Explanation explanation = new Explanation();
					String explanationText = "The file entitled "
							+ file.getTitle()
							+ ("(URI:")
							+ file.getURI()
							+ ") is recommended to you since users with similar tastes and intrests as yours (such as "
							+ similarUsersNames + ")" + " found it usefull";
					explanation.setExplanation(explanationText);
					recommendation.setExplanation(explanation);
					explanation.setTimestamp(new Date(System.currentTimeMillis()));
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
			userPreferences.setItemID(ratingIndex, file.getID());

			userPreferences.setValue(ratingIndex, 5);
			ratingIndex++;
		}

		for (String fileURI : user.getFiles()) {
			File file = this.model.getFileByURI(fileURI);
			if (file == null) {
				System.out.println("PROBLEM FILE " + file);
			} else {
				userPreferences.setItemID(ratingIndex, file.getID());
				userPreferences.setValue(ratingIndex, 5);
				ratingIndex++;
			}
		}

		return userPreferences;
	}

	public void close() {

	}
	
	public RecommenderParameters getInitializationParameters(){
		return this.recommenderParameters;
	}

}
