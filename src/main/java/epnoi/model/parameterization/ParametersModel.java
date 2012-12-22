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
	//private String indexPath;
	//private String graphPath;

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

	/*
	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
*/
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
/*
	public String getGraphPath() {
		return graphPath;
	}

	public void setGraphPath(String graphPath) {
		this.graphPath = graphPath;
	}
*/
	public InferenceEngineParameters getInferenceEngine() {
		return inferenceEngine;
	}

	public void setInferenceEngine(InferenceEngineParameters inferenceEngine) {
		this.inferenceEngine = inferenceEngine;
	}

	public void resolveToAbsolutePaths(Class<? extends Object> referenceClass) {
		String completeModelPath = referenceClass.getResource(this.modelPath)
				.getPath();

		this.setModelPath(completeModelPath);
		/*
		 * logger.info("The modelPath is made absolute: absolute value: " +
		 * parametersModel.getModelPath());
		 * 
		 * logger.info("The index Path is made absolute: intial value: " +
		 * parametersModel.getIndexPath());
		 */
		/*
		String indexPath = referenceClass.getResource(this.getIndexPath())
				.getPath();

		this.setIndexPath(indexPath);
		*/
		/*
		 * logger.info("The indexPath is made absolute: absolute value: " +
		 * parametersModel.getIndexPath());
		 * logger.info("The graph Path is made absolute: intial value: " +
		 * parametersModel.getGraphPath());
		 */
		/*
		String graphPath = referenceClass.getResource(this.getGraphPath())
				.getPath();

		this.setGraphPath(graphPath);
*/
		/*
		 * logger.info("The graph path is made absolute: absolute value: " +
		 * parametersModel.getGraphPath());
		 */
		for (KeywordRecommenderParameters keywordRecommender : this.keywordBasedRecommender) {
			keywordRecommender.setIndexPath(referenceClass.getResource(
					keywordRecommender.getIndexPath()).getPath());
			
			//System.out.println("KKKK "+keywordRecommender.getIndexPath());
		}
		
		for (SocialNetworkRecommenderParameters socialNetworkRecommender : this.socialRecommender) {
			socialNetworkRecommender.setGraphPath(referenceClass.getResource(
					socialNetworkRecommender.getGraphPath()).getPath());
		}
		
		for (GroupBasedRecommenderParameters groupBasedRecommender : this.groupBasedRecommender) {
			groupBasedRecommender.setIndexPath(referenceClass.getResource(
					groupBasedRecommender.getIndexPath()).getPath());
		}
	}
}
