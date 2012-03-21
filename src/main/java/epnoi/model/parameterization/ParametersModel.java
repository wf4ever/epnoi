package epnoi.model.parameterization;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElement;


//import javax.xml.bind.annotation.
@XmlRootElement(name = "parametersModel")

public class ParametersModel {
	
	/*
	 * Como un arraylist de propiedases
	public static final String MODEL_PATH_PROPERTY = "model.path";
	public static final String INDEX_PATH_PROPERTY = "index.path";
*/

	private String modelPath;
	private String indexPath;
	
	private ArrayList<CollaborativeFilterRecommenderParameters> collaborativeFilteringRecommender;
	

	public ParametersModel(){
		this.collaborativeFilteringRecommender = new ArrayList<CollaborativeFilterRecommenderParameters>();
	}

	public ArrayList<CollaborativeFilterRecommenderParameters> getCollaborativeFilteringRecommender() {
		return collaborativeFilteringRecommender;
	}

	public void setCollaborativeFilteringRecommender(
			ArrayList<CollaborativeFilterRecommenderParameters> collaborativeFilteringRecommender) {
		this.collaborativeFilteringRecommender = collaborativeFilteringRecommender;
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


	
}
