package epnoi.recommeders;

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

import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.Workflow;
import epnoi.model.parameterization.CollaborativeFilterRecommenderParameters;
import epnoi.model.parameterization.RecommenderParameters;

public class WorkflowsCollaborativeFilterRecommender implements
		CollaborativeFilterRecommender {

	Model model = null;
	UserSimilarity similarity = null;
	UserNeighborhood neighborhood = null;
	Recommender recommender = null;
	DataModel dataModel = null;
	Properties inizializationProperties = null;
	CollaborativeFilterRecommenderParameters recommenderParameters = null;

	public void init(Model model, RecommenderParameters recommenderParameters) {

		this.model = model;
		this.recommenderParameters = (CollaborativeFilterRecommenderParameters) recommenderParameters;
		_initData();

		try {
			if (epnoi.recommeders.Recommender.SIMILARITY_EUCLIDEAN
					.equals(this.recommenderParameters.getSimilarity())) {

				this.similarity = new EuclideanDistanceSimilarity(dataModel);

			} else if (epnoi.recommeders.Recommender.SIMILARITY_PEARSON_CORRELATION
					.equals(this.recommenderParameters.getSimilarity())) {
				this.similarity = new PearsonCorrelationSimilarity(dataModel);
			}

			if (epnoi.recommeders.Recommender.NEIGHBOURHOOD_TYPE_NEAREST.equals(this.recommenderParameters.getNeighbourhoodType())){
			Integer size = this.recommenderParameters.getNeighbourhoohdSize();
				neighborhood = new NearestNUserNeighborhood(size, similarity,
					dataModel);
			}else if (epnoi.recommeders.Recommender.NEIGHBOURHOOD_TYPE_THRESHOLD.equals(this.recommenderParameters.getNeighbourhoodType())){
				Float threshold = this.recommenderParameters.getNeighbourhoodThreshold();
				neighborhood = new ThresholdUserNeighborhood(threshold, similarity, dataModel);
			}
			recommender = new GenericUserBasedRecommender(dataModel,
					neighborhood, similarity);
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void init(Model model, Properties initializationProperties) {
		this.inizializationProperties = initializationProperties;
		this.model = model;
		System.out.println("(model)> " + model);
		_initData();

		try {
			similarity = new EuclideanDistanceSimilarity(dataModel);

			neighborhood = new NearestNUserNeighborhood(5, similarity,
					dataModel);

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
						user.getID(), 5);

				for (RecommendedItem recommendedItem : recommendations) {
					Recommendation recommendation = new Recommendation();
					recommendation.setUserURI(user.getURI());
					recommendation.setStrength(recommendedItem.getValue());
					recommendation.setItemID(recommendedItem.getItemID());

					Parameter parameter = new Parameter();
					parameter.setName("technique");
					parameter.setValue("collaborative-filtering");
					recommendation.getProvenance().getParameters()
							.add(parameter);

					Workflow workflow = this.model
							.getWorkflowByID(recommendation.getItemID());
					recommendation.setItemURI(workflow.getURI());

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

		int numberOfFavouritedWorkflows = user.getFavouritedWorkflows().size();

		int numberOfRatedItems = model.getRatingsByUser(user.getURI(),
				Rating.WORKFLOW_RATING).size();
		int numberOfOwnedWorkflows = user.getWorkflows().size();

		PreferenceArray userPreferences = new GenericUserPreferenceArray(
				numberOfFavouritedWorkflows + numberOfRatedItems
						+ numberOfOwnedWorkflows);

		userPreferences.setUserID(0, user.getID());
		int ratingIndex = 0;

		for (Rating rating : model.getRatingsByUser(user.getURI(),
				Rating.WORKFLOW_RATING)) {

			userPreferences.setItemID(ratingIndex, rating.getRatedElementID());
			// System.out.println("----------->" + rating.getRatingValue());
			userPreferences.setValue(ratingIndex, rating.getRatingValue());
			ratingIndex++;
		}

		for (String workflowURI : user.getFavouritedWorkflows()) {
			Workflow workflow = this.model.getWorkflowByURI(workflowURI);
			userPreferences.setItemID(ratingIndex, workflow.getID());

			userPreferences.setValue(ratingIndex, 5);
			ratingIndex++;
		}

		for (String workflowURI : user.getWorkflows()) {
			Workflow workflow = this.model.getWorkflowByURI(workflowURI);
			if (workflow == null) {
				System.out.println("PROBLEMA " + workflowURI);
			} else {
				userPreferences.setItemID(ratingIndex, workflow.getID());
				userPreferences.setValue(ratingIndex, 5);
				ratingIndex++;
			}
		}
		// System.out.println(user.getID() + "UserPreferences for "+
		// userPreferences);
		return userPreferences;
	}

	public void close() {

	}

}
