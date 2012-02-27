package epnoi.tools;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.Rating;
import epnoi.model.User;
import epnoi.model.Workflow;

public class CollaborativeFilteringRecommenderEvaluator {

	private String MODEL_PATH = "/myExperimentModel.xml";
	Model model = null;
	DataModel dataModel = null;
	
RecommenderEvaluator evaluatorA = new AverageAbsoluteDifferenceRecommenderEvaluator();

	RecommenderIRStatsEvaluator evaluatorPR = new GenericRecommenderIRStatsEvaluator();

	RecommenderBuilder builder = new RecommenderBuilder() {

		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {

			UserSimilarity similarity = new EuclideanDistanceSimilarity(
					dataModel);
			// UserSimilarity similarity = new
			// PearsonCorrelationSimilarity(dataModel);

			UserNeighborhood neighborhood = new NearestNUserNeighborhood(5,
					similarity, dataModel);
			return new GenericUserBasedRecommender(dataModel, neighborhood,
					similarity);
		}
	};

	public void init(String filePath) {
		this.model = ModelReader.read(filePath);
		_initData();

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

			userPreferences.setValue(ratingIndex, rating.getRatingValue());
			ratingIndex++;
		}

		for (String workflowURI : user.getFavouritedWorkflows()) {
			Workflow workflow = this.model.getWorkflowByURI(workflowURI);
			userPreferences.setItemID(ratingIndex, workflow.getID());

			userPreferences.setValue(ratingIndex, 4);
			ratingIndex++;
		}

		for (String workflowURI : user.getWorkflows()) {
			Workflow workflow = this.model.getWorkflowByURI(workflowURI);
			userPreferences.setItemID(ratingIndex, workflow.getID());
			userPreferences.setValue(ratingIndex, 5);
			ratingIndex++;
		}

		// System.out.println(user.getID() + "UserPreferences for "+
		// userPreferences);
		return userPreferences;
	}

	public void evaluate() {
		RandomUtils.useTestSeed();
		IRStatistics stats = null;
		try {
			stats = evaluatorPR.evaluate(builder, null, this.dataModel, null, 2,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Precision " + stats.getPrecision());
		System.out.println("Recall " + stats.getRecall());
		
		double score=0.0;
		try {
			score = evaluatorA.evaluate(builder, null, dataModel,0.9, 1.0);
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Average "+score);
		
	}

	public static void main(String[] args) {
		System.out
				.println("Starting the evaluation of the Collaborative Filter");
		CollaborativeFilteringRecommenderEvaluator evaluator = new CollaborativeFilteringRecommenderEvaluator();
		evaluator.init("/model.xml");
		evaluator.evaluate();

	}

}
