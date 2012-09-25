package epnoi.model;

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendationSpace {
	HashMap<String, ArrayList<Recommendation>> recommendationsByURI;
	HashMap<String, ArrayList<Recommendation>> recommendationsByName;
	HashMap<Long, ArrayList<Recommendation>> recommendationsByID;

	// -------------------------------------------------------------------------------------------------------

	public RecommendationSpace() {
		this.recommendationsByURI = new HashMap<String, ArrayList<Recommendation>>();
		this.recommendationsByName = new HashMap<String, ArrayList<Recommendation>>();
		this.recommendationsByID = new HashMap<Long, ArrayList<Recommendation>>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ArrayList<Recommendation> getAllRecommendations() {
		ArrayList<Recommendation> allRecommendation = new ArrayList<Recommendation>();

		for (ArrayList<Recommendation> recommendationsForUSer : this.recommendationsByURI
				.values()) {
			for (Recommendation recommendation : recommendationsForUSer) {
				allRecommendation.add(recommendation);
			}
		}
		return allRecommendation;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ArrayList<Recommendation> getRecommendationsForUserURI(String userURI) {
		if (this.recommendationsByURI.get(userURI) != null)
			return this.recommendationsByURI.get(userURI);
		else
			return new ArrayList<Recommendation>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ArrayList<Recommendation> getRecommendationsForUserID(Long userID) {
		if (this.recommendationsByID.get(userID) != null)
			return this.recommendationsByID.get(userID);
		else
			return new ArrayList<Recommendation>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * 
 * @param userID The myExperiment integer ID  of the user
 * @param type The type of the recommended itms (i.e. Provenance.ITEM_TYPE_USER, Provenance.ITEM_TYPE_WORKFLOW, Provenance.ITEM_TYPE_FILE, Provenance.ITEM_TYPE_PACK)
 * @return The list with all the recommendations of such type to the user
 */
	public ArrayList<Recommendation> getRecommendationsForUserID(Long userID,
			String type) {
		if (this.recommendationsByID.get(userID) != null) {
			ArrayList<Recommendation> recommendationsForUser = new ArrayList<Recommendation>();
			for (Recommendation recommendation : this.recommendationsByID
					.get(userID)) {
				if (recommendation.getProvenance()
						.getParameterByName(Provenance.ITEM_TYPE).equals(type)) {
					recommendationsForUser.add(recommendation);
				}
			}
			return recommendationsForUser;
		}

		else
			return new ArrayList<Recommendation>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param userURI The URI of the user
	 * @param type The type of the recommended items (i.e. Provenance.ITEM_TYPE_USER, Provenance.ITEM_TYPE_WORKFLOW, Provenance.ITEM_TYPE_FILE, Provenance.ITEM_TYPE_PACK)
	 * @return The list with all the recommendations of such type to the user
	 */
	public ArrayList<Recommendation> getRecommendationsForUserURI(
			String userURI, String type) {
		if (this.recommendationsByURI.get(userURI) != null) {
			ArrayList<Recommendation> recommendationsForUser = new ArrayList<Recommendation>();
			for (Recommendation recommendation : this.recommendationsByURI
					.get(userURI)) {
				if (recommendation.getProvenance()
						.getParameterByName(Provenance.ITEM_TYPE).equals(type)) {
					recommendationsForUser.add(recommendation);
				}
			}
			return recommendationsForUser;
		}

		else
			return new ArrayList<Recommendation>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ArrayList<Recommendation> getRecommendationsForUserName(
			String userName) {
		if (this.recommendationsByName.get(userName) != null)
			return this.recommendationsByName.get(userName);
		else
			return new ArrayList<Recommendation>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void addRecommendationForUser(User user,
			Recommendation recommendation) {
		ArrayList<Recommendation> recommendationsForUser = null;

		recommendationsForUser = this.recommendationsByURI.get(user.getURI());
		if (recommendationsForUser == null) {
			recommendationsForUser = new ArrayList<Recommendation>();
			this.recommendationsByURI
					.put(user.getURI(), recommendationsForUser);
		}
		recommendationsForUser.add(recommendation);

		ArrayList<Recommendation> recommendationsForUserByID = null;
		recommendationsForUserByID = this.recommendationsByID.get(user.getID());
		if (recommendationsForUserByID == null) {
			recommendationsForUserByID = new ArrayList<Recommendation>();
			this.recommendationsByID.put(user.getID(),
					recommendationsForUserByID);
		}
		recommendationsForUserByID.add(recommendation);

		ArrayList<Recommendation> recommendationsForUserName = null;

		recommendationsForUserName = this.recommendationsByName.get(user
				.getName());
		if (recommendationsForUserName == null) {
			recommendationsForUserName = new ArrayList<Recommendation>();
			this.recommendationsByName.put(user.getName(),
					recommendationsForUserName);
		}
		recommendationsForUserName.add(recommendation);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void addRecommendationsForUser(User user,
			ArrayList<Recommendation> recommendation) {
		ArrayList<Recommendation> recommendationsForUser = null;

		recommendationsForUser = this.recommendationsByURI.get(user.getURI());
		if (recommendationsForUser == null) {
			recommendationsForUser = new ArrayList<Recommendation>();
			this.recommendationsByURI
					.put(user.getURI(), recommendationsForUser);
		}
		recommendationsForUser.addAll(recommendation);

		ArrayList<Recommendation> recommendationsForUserByID = null;
		recommendationsForUserByID = this.recommendationsByID.get(user.getID());
		if (recommendationsForUserByID == null) {
			recommendationsForUserByID = new ArrayList<Recommendation>();
			this.recommendationsByID.put(user.getID(),
					recommendationsForUserByID);
		}
		recommendationsForUserByID.addAll(recommendation);

		ArrayList<Recommendation> recommendationsForUserName = null;

		recommendationsForUserName = this.recommendationsByName.get(user
				.getName());
		if (recommendationsForUserName == null) {
			recommendationsForUserName = new ArrayList<Recommendation>();
			this.recommendationsByName.put(user.getName(),
					recommendationsForUserName);
		}
		recommendationsForUserName.addAll(recommendation);
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void removeRecommendationsForUserURI(String userURI) {
		this.recommendationsByURI.put(userURI, new ArrayList<Recommendation>());

	}

}
