package epnoi.model.parameterization;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

//import javax.xml.bind.annotation.
@XmlRootElement(name = "parametersModel")
public class ParametersModel {

	/*
	 * public static final String HOSTNAME_PROPERTY = "server.hostname"; public
	 * static final String PORT_PROPERTY = "server.port"; public static final
	 * String PATH_PROPERTY = "server.path"; public static final String
	 * MODEL_PATH_PROPERTY = "model.path"; public static final String
	 * INDEX_PATH_PROPERTY = "index.path"; Como un arraylist de propiedases
	 * public static final String MODEL_PATH_PROPERTY = "model.path"; public
	 * static final String INDEX_PATH_PROPERTY = "index.path";
	 */

	private String modelPath;
	private String indexPath;
	private String graphPath;

	// Server related properties
	private String hostname;
	private String port;
	private String path;
	

	private ArrayList<CollaborativeFilterRecommenderParameters> collaborativeFilteringRecommender;
	private ArrayList<KeywordRecommenderParameters> keywordBasedRecommender;
	private ArrayList<SocialNetworkRecommenderParameters> socialRecommender;
	private ArrayList<GroupBasedRecommenderParameters> groupBasedRecommender;
	private InferenceEngineParameters inferenceEngine;

	public ParametersModel() {
		this.collaborativeFilteringRecommender = new ArrayList<CollaborativeFilterRecommenderParameters>();
		this.keywordBasedRecommender = new ArrayList<KeywordRecommenderParameters>();
		this.socialRecommender = new ArrayList<SocialNetworkRecommenderParameters>();
		this.groupBasedRecommender = new ArrayList<GroupBasedRecommenderParameters>();
	}

	public ArrayList<KeywordRecommenderParameters> getKeywordBasedRecommender() {
		return keywordBasedRecommender;
	}

	public void setKeywordBasedRecommender(
			ArrayList<KeywordRecommenderParameters> keywordBasedRecommender) {
		this.keywordBasedRecommender = keywordBasedRecommender;
	}

	public ArrayList<CollaborativeFilterRecommenderParameters> getCollaborativeFilteringRecommender() {
		return collaborativeFilteringRecommender;
	}

	public void setCollaborativeFilteringRecommender(
			ArrayList<CollaborativeFilterRecommenderParameters> collaborativeFilteringRecommender) {
		this.collaborativeFilteringRecommender = collaborativeFilteringRecommender;
	}

	public ArrayList<SocialNetworkRecommenderParameters> getSocialRecommender() {
		return socialRecommender;
	}

	public void setSocialRecommender(
			ArrayList<SocialNetworkRecommenderParameters> socialRecommender) {
		this.socialRecommender = socialRecommender;
	}

	public ArrayList<GroupBasedRecommenderParameters> getGroupBasedRecommender() {
		return groupBasedRecommender;
	}

	public void setGroupBasedRecommender(
			ArrayList<GroupBasedRecommenderParameters> groupBasedRecommender) {
		this.groupBasedRecommender = groupBasedRecommender;
	}

	
	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getGraphPath() {
		return graphPath;
	}

	public void setGraphPath(String graphPath) {
		this.graphPath = graphPath;
	}

	public InferenceEngineParameters getInferenceEngine() {
		return inferenceEngine;
	}

	public void setInferenceEngine(InferenceEngineParameters inferenceEngine) {
		this.inferenceEngine = inferenceEngine;
	}


	
}
