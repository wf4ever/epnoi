package epnoi.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.User;

public class MyExperimentSocialNetworkHarvester {
	private GraphDatabaseService database;
	private Model model;

	private String MODEL_PATH = "/wf4ever/data/lastImportedModel.xml";
	private static final String DB_PATH = "/JUNK/myexperimentgraphdb";
	private static final String URI_KEY = "URI";
	private static final String LABEL_KEY = "Label";
	private static Index<Node> nodeIndex;
	Node usersReferenceNode;

	private static enum RelTypes implements RelationshipType {
		USERS_REFERENCE, USER, FRIEND
	}

	public void run() {
		Transaction tx = database.beginTx();
		try {
			for (User user : this.model.getUsers()) {
				Node userNode = _createUserNode(user);

			}

			for (User user : this.model.getUsers()) {
				System.out.println("Adding user " + user.getURI()
						+ " to the graph");
				Node userNode = _getNodeFromIndex(user.getURI());
				for (String friendURI : user.getFriends()) {
					Node friendNode = _getNodeFromIndex(friendURI);
					// Since there can be some friends that could not be stored
					// in the model
					// we must check whether there is no node that represent
					// that user (i.e. friendNode != null)
					if (friendNode != null) {
						Iterator<Relationship> relationsIt = friendNode
								.getRelationships().iterator();
						boolean finded = false;
						while (relationsIt.hasNext() && (!finded)) {
							Relationship relationship = relationsIt.next();
							finded = relationship.getEndNode().equals(userNode)
									&& relationship.getType().equals(
											RelTypes.FRIEND);

						}
						if (!finded) {
							userNode.createRelationshipTo(friendNode,
									RelTypes.FRIEND);
						}
					}
				}

			}

			tx.success();
		} finally {
			tx.finish();
		}

	}

	private void init() {

		try {
			this.model = ModelReader.read(this.MODEL_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("The epnoi model has been read in "
				+ this.MODEL_PATH);
		database = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);

		System.out.println("The neo4j database has been itialized in "
				+ this.MODEL_PATH);

		nodeIndex = database.index().forNodes("users");
		System.out.println("The neo4j index has been itialized");

		registerShutdownHook();

		Transaction tx = database.beginTx();
		// Create users sub reference node
		try {
			Node refNode = database.getReferenceNode();
			refNode.setProperty(URI_KEY, "reference node");
			refNode.setProperty(LABEL_KEY, "reference node");

			tx.success();
		} finally {
			tx.finish();
		}

	}

	private void registerShutdownHook() {
		// Registers a shutdown hook for the Neo4j and index service instances
		// so that it shuts down nicely when the VM exits (even if you
		// "Ctrl-C" the running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				database.shutdown();
			}
		});
	}

	private Node _createUserNode(User user) {

		Node retrievedNode = _getNodeFromIndex(user.getURI());
		if (retrievedNode != null) {
			System.out.println("YA ESTABA! " + user.getURI());
			return retrievedNode;
		}

		Node newNode = database.createNode();
		newNode.setProperty(URI_KEY, user.getURI());
		if (user.getName() != null) {
			newNode.setProperty(LABEL_KEY, user.getName());
		} else {
			newNode.setProperty(LABEL_KEY, "");
		}
		nodeIndex.add(newNode, URI_KEY, user.getURI());

		return newNode;
	}

	public Node _getNodeFromIndex(String URI) {
		
		IndexHits<Node> hits = this.nodeIndex.get(URI_KEY, URI);
		Node node = hits.getSingle();
		hits.close();
		return node;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public GraphDatabaseService getDatabase() {
		return database;
	}

	public void setDatabase(GraphDatabaseService database) {
		this.database = database;
	}

	public ArrayList<String> getPossibleCandidates(User user) {

		ArrayList<String> candidates = new ArrayList<String>();
		String query = "START user=node:users(URI = {userURI}) "
				+ "MATCH user-[:FRIEND]-x-[:FRIEND]-candidate "
				+ "WHERE not(user-[:FRIEND]-candidate) "
				+ "RETURN distinct(candidate)" + " as candidates";

		// (user.URL!=candidate.URL) and

		ExecutionEngine engine = new ExecutionEngine(database);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userURI", user.getURI());
		ExecutionResult result = engine.execute(query, params);

		// System.out.println(result);

		Iterator<Node> numberOfEdges = result.columnAs("candidates");
		/*
		 * System.out.println("Candidates for "+user.getName()+
		 * " --------------------------------"); if (numberOfEdges.hasNext()){
		 * System.out.println("Candidates for "+user.getName()+
		 * " **************************************"); }
		 */
		while (numberOfEdges.hasNext()) {
			Node candidate = numberOfEdges.next();
			candidates.add((String) candidate.getProperty(URI_KEY));
		}
		/*
		 * String queryFriends = "START user=node:users(URI = {userURI}) " +
		 * "MATCH user-[:FRIEND]-friend " + "RETURN distinct(friend)"
		 * +" as friends";
		 * 
		 * 
		 * 
		 * result = engine.execute(queryFriends, params);
		 * 
		 * 
		 * Iterator<Node> friendsIt = result.columnAs("friends");
		 * System.out.println
		 * ("Friends for "+user.getName()+" --------------------------------");
		 * if (friendsIt.hasNext()){
		 * System.out.println("Friends for "+user.getName
		 * ()+" **************************************"); } while
		 * (friendsIt.hasNext()) { Node candidate = friendsIt.next();
		 * System.out.println("		"+candidate.getProperty("Label")); }
		 */
		return candidates;
	}

	public static void main(String[] args) throws Exception {

		System.out.println("Harvesting the myExperiment social network");

		MyExperimentSocialNetworkHarvester harvester = new MyExperimentSocialNetworkHarvester();
		harvester.init();
		harvester.run();

		long end = new Date().getTime();

		// System.out.println("Detecting candidates ");
		System.exit(0);

		HashMap<String, Long> mutualFriendsGraphCardinalityCache = new HashMap<String, Long>();
		ExecutionEngine engine = new ExecutionEngine(harvester.getDatabase());
		for (User user : harvester.getModel().getUsers()) {
			// System.out.println(">" + user.getName());
			if (user.getFriends().size() != 0) {
				Long numberOfEdgesFriendsGraph = harvester
						.userFriendsGraphEdgesCardinalityOptimized(
								user.getURI(), engine);
				for (String candidateURI : harvester
						.getPossibleCandidates(user)) {
					User candidateUser = harvester.getModel().getUserByURI(
							candidateURI);
					// System.out.println("      candidate:"
					// + candidateUser.getName());
					if (!candidateURI.equals(user.getURI())) {

						Double similarity = harvester
								.userNetworkSimilarityOptimized(user.getURI(),
										candidateURI,
										numberOfEdgesFriendsGraph,
										mutualFriendsGraphCardinalityCache,
										engine);

						System.out.println(">     >(user, similarity)> ("
								+ user.getName() + ", "
								+ candidateUser.getName() + ") >" + similarity);
					}
				}
			}
		}
		long start = new Date().getTime();
		System.out
				.println("Harvesting took " + (end - start) + " milliseconds");
	}

	private Long userFriendsGraphEdgesCardinality(String user) {
		Long cardinality = 0L;
		String query = "START user=node:users(URI = {userURI}) "
				+ "MATCH user-[r:FRIEND]-x "
				+ "RETURN count(distinct(r)) as numberOfEdges";
		ExecutionEngine engine = new ExecutionEngine(database);
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

	private Long usersMutualFriendsGraphEdgesCardinality(String originUser,
			String destinationUser) {
		Long cardinality = 0L;

		String query = "START originUser=node:users(URI = {originUser}), destinationUser=node:users(URI = {destinationUser}) "
				+ "MATCH originUser-[r:FRIEND]-x-[s:FRIEND]-destinationUser "
				+ "RETURN count(distinct(r)) as numberOfR, count(distinct(s)) as numberOfS";

		ExecutionEngine engine = new ExecutionEngine(database);
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

	private Long usersMutualFriendsGraphEdgesCardinalityOptimized(
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

	public Double userNetworkSimilarity(String originUser,
			String destinationUser) {
		Double similarity = 0.;
		Long numberOfEdgesFriendsGraph = userFriendsGraphEdgesCardinality(originUser);

		Long numberOfEdgesMutualFriendsGraph = usersMutualFriendsGraphEdgesCardinality(
				originUser, destinationUser);
		System.out.println("#FG " + (numberOfEdgesFriendsGraph));
		System.out.println("#MFG " + (numberOfEdgesMutualFriendsGraph));
		// double similarity =
		// Math.log(numberOfEdgesMutualFriendsGraph)/Math.log(2*numberOfEdgesFriendsGraph);
		similarity = ((double) Math.log(numberOfEdgesMutualFriendsGraph) / ((double) Math
				.log(2 * numberOfEdgesFriendsGraph)));
		System.out.println("Similarity" + similarity);
		return similarity;
	}

	private Long userFriendsGraphEdgesCardinalityOptimized(String user,
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

	public Double userNetworkSimilarityOptimized(String originUser,
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

			numberOfEdgesMutualFriendsGraph = usersMutualFriendsGraphEdgesCardinalityOptimized(
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

}
