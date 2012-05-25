package epnoi.recommeders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.parameterization.KeywordRecommenderParameters;
import epnoi.model.parameterization.RecommenderParameters;
import epnoi.model.parameterization.SocialNetworkRecommenderParameters;
import epnoi.tools.MyExperimentSocialNetworkHarvester;

public class UsersSocialNetworkRecommender implements Recommender {
	SocialNetworkRecommenderParameters recommenderParameters = null;
	Model model = null;
	private GraphDatabaseService database;

	private String MODEL_PATH = "/proofs/lastImportedModel.xml";
	private static final String DB_PATH = "/proofs/myexperimentgraphdb";
	private static final String URI_KEY = "URI";
	private static final String LABEL_KEY = "Label";
	private static Index<Node> nodeIndex;
	Node usersReferenceNode;

	public UsersSocialNetworkRecommender(
			RecommenderParameters initializationParameters) {
		this.recommenderParameters = (SocialNetworkRecommenderParameters) initializationParameters;
	}

	public void recommend(RecommendationSpace recommedationSpace) {
		System.out.println("Harvesting the myExperiment social network");

		int numberOfRecommendations = 0;

		long end = new Date().getTime();

		System.out.println("Detecting candidates ");
		// System.exit(0);

		HashMap<String, Long> mutualFriendsGraphCardinalityCache = new HashMap<String, Long>();
		ExecutionEngine engine = new ExecutionEngine(this.database);
		for (User user : model.getUsers()) {
			ArrayList<Recommendation> recommendations = new ArrayList<Recommendation>();
			// System.out.println(">" + user.getName());
			if (user.getFriends().size() != 0) {
				Long numberOfEdgesFriendsGraph = this
						._userFriendsGraphEdgesCardinalityOptimized(
								user.getURI(), engine);

				ArrayList<String> candidates = _getPossibleCandidates(user,
						engine);
				/*
				System.out
						.println("(" + candidates.size() + ">> " + candidates);
				*/
				for (String candidateURI : candidates) {
					User candidateUser = this.model.getUserByURI(candidateURI);
					// System.out.println("      candidate:"
					// + candidateUser.getName());
					if (!candidateURI.equals(user.getURI())) {

						Double similarity = _userNetworkSimilarityOptimized(
								user.getURI(), candidateURI,
								numberOfEdgesFriendsGraph,
								mutualFriendsGraphCardinalityCache, engine);

						Recommendation recommendation = new Recommendation();
						recommendation.setUserURI(user.getURI());
						recommendation.setStrength(similarity.floatValue());
						recommendation.setItemURI(candidateURI);
						recommendation
								.setRecommenderURI(this.recommenderParameters
										.getURI());
						Parameter parameter = new Parameter();
						parameter.setName("technique");
						parameter.setValue("social-network");
						recommendation.getProvenance().getParameters()
								.add(parameter);

						recommendations.add(recommendation);
						/*
						 * System.out.println(">     >(user, similarity)> (" +
						 * user.getName() + ", " + candidateUser.getName() +
						 * ") >" + similarity);
						 */
					}

				}
				
				ArrayList<Recommendation> orderedRecommendations = _orderAndNormalize(recommendations);
				if (orderedRecommendations.size() > 0) {
					Float maxStrength = orderedRecommendations.get(0)
							.getStrength();
					int index = 0;
					while (index < this.recommenderParameters
							.getNumberOfRecommendations()
							&& index < recommendations.size()) {
						System.out.println(user.getName() + "> "
								+ orderedRecommendations.get(index));
						numberOfRecommendations++;
						orderedRecommendations.get(index).
						setStrength(orderedRecommendations.get(index).getStrength()/maxStrength);	
						recommedationSpace.addRecommendationForUser(user,
								orderedRecommendations.get(index));
						index++;
					}
				}
			}

		}

		long start = new Date().getTime();
		System.out
				.println("Harvesting took " + (end - start) + " milliseconds");
		System.out.println("NUMBER OF RECOMMENDATIONS "
				+ numberOfRecommendations);

	}

	private ArrayList<Recommendation> _orderAndNormalize(
			ArrayList<Recommendation> recomendations) {
		ArrayList<Recommendation> orderedRecommendations = (ArrayList<Recommendation>) recomendations
				.clone();
		Collections.sort(orderedRecommendations);
		Collections.reverse(orderedRecommendations);
		if (orderedRecommendations.size() > 0) {
			Recommendation firstRecommnedation = orderedRecommendations.get(0);

			for (Recommendation orderedRecommendation : orderedRecommendations) {

			}
		}
		return orderedRecommendations;

	}

	public void init(Model model) {
		this.model = model;
		System.out.println("Database path "
				+ this.recommenderParameters.getDatabasePath());
		this.database = new GraphDatabaseFactory()
				.newEmbeddedDatabase(this.recommenderParameters
						.getDatabasePath());
		this.nodeIndex = database.index().forNodes("users");

	}

	public void close() {

	}

	public RecommenderParameters getInitializationParameters() {

		return this.recommenderParameters;
	}

	private Long _userFriendsGraphEdgesCardinalityOptimized(String user,
			ExecutionEngine engine) {
		Long cardinality = 0L;
		String query = "START user=node:users(URI = {userURI}) "
				+ "MATCH user-[r:FRIEND]-x "
				+ "RETURN count(distinct(r)) as numberOfEdges";
		// ExecutionEngine engine = new ExecutionEngine(database);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userURI", user);
		ExecutionResult result = engine.execute(query, params);

		// System.out.println(result);

		Iterator<Long> numberOfEdges = result.columnAs("numberOfEdges");
		if (numberOfEdges.hasNext()) {
			cardinality = numberOfEdges.next();
		}
		return cardinality;
	}

	public ArrayList<String> _getPossibleCandidates(User user,
			ExecutionEngine engine) {

		ArrayList<String> candidates = new ArrayList<String>();
		String query = "START user=node:users(URI = {userURI}) "
				+ "MATCH user-[:FRIEND]-x-[:FRIEND]-candidate "
				+ "WHERE not(user-[:FRIEND]-candidate) "
				+ "RETURN distinct(candidate)" + " as candidates";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userURI", user.getURI());
		ExecutionResult result = engine.execute(query, params);

		Iterator<Node> numberOfEdges = result.columnAs("candidates");
		while (numberOfEdges.hasNext()) {
			Node candidate = numberOfEdges.next();
			candidates.add((String) candidate.getProperty(URI_KEY));
		}
		return candidates;
	}

	public Double _userNetworkSimilarityOptimized(String originUser,
			String destinationUser, Long numberOfEdgesFriendsGraph,
			HashMap<String, Long> mfgCache, ExecutionEngine engine) {
		Double similarity = 0.;

		Long numberOfEdgesMutualFriendsGraph = 0L;

		if (mfgCache.containsKey(originUser + destinationUser)) {
			numberOfEdgesMutualFriendsGraph = mfgCache.get(originUser
					+ destinationUser);
		} else if (mfgCache.containsKey(destinationUser + originUser))
			numberOfEdgesMutualFriendsGraph = mfgCache.get(destinationUser
					+ originUser);

		else {

			numberOfEdgesMutualFriendsGraph = _usersMutualFriendsGraphEdgesCardinalityOptimized(
					originUser, destinationUser, engine);
			mfgCache.put(originUser + destinationUser,
					numberOfEdgesMutualFriendsGraph);
			mfgCache.put(destinationUser + originUser,
					numberOfEdgesMutualFriendsGraph);
			// System.out.println("No esta, la meto "+numberOfEdgesMutualFriendsGraph);
		}

		/*
		 * numberOfEdgesMutualFriendsGraph =
		 * usersMutualFriendsGraphEdgesCardinalityOptimized( originUser,
		 * destinationUser, engine);
		 */
		// System.out.println("#FG " + (numberOfEdgesFriendsGraph));
		// System.out.println("#MFG " + (numberOfEdgesMutualFriendsGraph));
		// double similarity =
		// Math.log(numberOfEdgesMutualFriendsGraph)/Math.log(2*numberOfEdgesFriendsGraph);
		similarity = ((double) Math.log(numberOfEdgesMutualFriendsGraph) / ((double) Math
				.log(2 * numberOfEdgesFriendsGraph)));
		// System.out.println("Similarity" + similarity);
		return similarity;
	}

	private Long _usersMutualFriendsGraphEdgesCardinalityOptimized(
			String originUser, String destinationUser, ExecutionEngine engine) {
		Long cardinality = 0L;

		String query = "START originUser=node:users(URI = {originUser}), destinationUser=node:users(URI = {destinationUser}) "
				+ "MATCH originUser-[r:FRIEND]-x-[s:FRIEND]-destinationUser "
				+ "RETURN count(distinct(r)) as numberOfR, count(distinct(s)) as numberOfS";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("originUser", originUser);
		params.put("destinationUser", destinationUser);
		// System.out.println("..................> "+params);
		ExecutionResult result = engine.execute(query, params);

		// System.out.println(result);
		Long numberOfRs = 0L;
		Long numberOfSs = 0L;

		if (result.iterator().hasNext()) {
			Map resultMap = result.iterator().next();
			numberOfRs = (Long) resultMap.get("numberOfR");
			numberOfSs = (Long) resultMap.get("numberOfS");

		}

		String secondQuery = "START originUser=node:users(URI = {originUser}), destinationUser=node:users(URI = {destinationUser}) "
				+ "MATCH originUser-[:FRIEND]-x-[t:FRIEND]-y "
				+ " WHERE (y-[:FRIEND]-destinationUser) "
				+ "RETURN count(distinct(t)) as numberOfTs";

		result = engine.execute(secondQuery, params);

		Long numberOfTs = 0L;
		if (result.iterator().hasNext()) {
			numberOfTs = (Long) result.iterator().next().get("numberOfTs");
		}

		// System.out.println("out Rs> "+numberOfRs+" Ss> "+numberOfSs+" Ts> "+numberOfTs);

		return numberOfRs + numberOfSs + numberOfTs;
	}
}
