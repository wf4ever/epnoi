package epnoi.model.parameterization;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

//import javax.xml.bind.annotation.
@XmlRootElement(name = "parametersModel")
public class ParametersModel {

	private String modelPath;
	// private String indexPath;
	// private String graphPath;

	// Server related properties
	private String hostname;
	private String port;
	private String path;
	// private String scope;

	private ArrayList<CollaborativeFilterRecommenderParameters> collaborativeFilteringRecommender;
	private ArrayList<KeywordRecommenderParameters> keywordBasedRecommender;
	private ArrayList<SocialNetworkRecommenderParameters> socialRecommender;
	private ArrayList<GroupBasedRecommenderParameters> groupBasedRecommender;
	private ArrayList<AggregationBasedRecommenderParameters> aggregationBasedRecommender;
	private InferenceEngineParameters inferenceEngine;

	public ParametersModel() {
		this.collaborativeFilteringRecommender = new ArrayList<CollaborativeFilterRecommenderParameters>();
		this.keywordBasedRecommender = new ArrayList<KeywordRecommenderParameters>();
		this.socialRecommender = new ArrayList<SocialNetworkRecommenderParameters>();
		this.groupBasedRecommender = new ArrayList<GroupBasedRecommenderParameters>();
		this.aggregationBasedRecommender = new ArrayList<AggregationBasedRecommenderParameters>();
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

	public String getModelPath() {
		return modelPath;
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}

	/*
	 * public String getIndexPath() { return indexPath; }
	 * 
	 * public void setIndexPath(String indexPath) { this.indexPath = indexPath;
	 * }
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
	 * public String getGraphPath() { return graphPath; }
	 * 
	 * public void setGraphPath(String graphPath) { this.graphPath = graphPath;
	 * }
	 */
	public InferenceEngineParameters getInferenceEngine() {
		return inferenceEngine;
	}

	public void setInferenceEngine(InferenceEngineParameters inferenceEngine) {
		this.inferenceEngine = inferenceEngine;
	}

	public void resolveToAbsolutePaths(Class<? extends Object> referenceClass) {
		String completeModelPath = this.modelPath;
		if (this.modelPath.charAt(0) != '/') {
			completeModelPath = referenceClass.getResource(this.modelPath)
					.getPath();
		}
		this.setModelPath(completeModelPath);
		/*
		 * logger.info("The modelPath is made absolute: absolute value: " +
		 * parametersModel.getModelPath());
		 * 
		 * logger.info("The index Path is made absolute: intial value: " +
		 * parametersModel.getIndexPath());
		 */
		/*
		 * String indexPath = referenceClass.getResource(this.getIndexPath())
		 * .getPath();
		 * 
		 * this.setIndexPath(indexPath);
		 */
		/*
		 * logger.info("The indexPath is made absolute: absolute value: " +
		 * parametersModel.getIndexPath());
		 * logger.info("The graph Path is made absolute: intial value: " +
		 * parametersModel.getGraphPath());
		 */
		/*
		 * String graphPath = referenceClass.getResource(this.getGraphPath())
		 * .getPath();
		 * 
		 * this.setGraphPath(graphPath);
		 */
		/*
		 * logger.info("The graph path is made absolute: absolute value: " +
		 * parametersModel.getGraphPath());
		 */
		for (KeywordRecommenderParameters keywordRecommender : this.keywordBasedRecommender) {

			if (keywordRecommender.getIndexPath().charAt(0) != '/') {
				keywordRecommender.setIndexPath(referenceClass.getResource(
						keywordRecommender.getIndexPath()).getPath());
			}

		}

		for (SocialNetworkRecommenderParameters socialNetworkRecommender : this.socialRecommender) {
			if (socialNetworkRecommender.getGraphPath().charAt(0) != '/') {
				socialNetworkRecommender.setGraphPath(referenceClass
						.getResource(socialNetworkRecommender.getGraphPath())
						.getPath());
			}
		}

		for (GroupBasedRecommenderParameters groupBasedRecommender : this.groupBasedRecommender) {
			if (groupBasedRecommender.getIndexPath().charAt(0) != '/') {
				groupBasedRecommender.setIndexPath(referenceClass.getResource(
						groupBasedRecommender.getIndexPath()).getPath());
			}
		}

		for (AggregationBasedRecommenderParameters aggregationBasedRecommender : this.aggregationBasedRecommender) {
			if (aggregationBasedRecommender.getIndexPath().charAt(0) != '/') {

				aggregationBasedRecommender
						.setIndexPath(referenceClass.getResource(
								aggregationBasedRecommender.getIndexPath())
								.getPath());

			}
		}
	}

	public ArrayList<AggregationBasedRecommenderParameters> getAggregationBasedRecommender() {
		return aggregationBasedRecommender;
	}

	public void setAggregationBasedRecommender(
			ArrayList<AggregationBasedRecommenderParameters> aggregationBasedRecommender) {
		this.aggregationBasedRecommender = aggregationBasedRecommender;
	}

	public ArrayList<GroupBasedRecommenderParameters> getGroupBasedRecommender() {
		return groupBasedRecommender;
	}

	public void setGroupBasedRecommender(
			ArrayList<GroupBasedRecommenderParameters> groupBasedRecommender) {
		this.groupBasedRecommender = groupBasedRecommender;
	}
}
