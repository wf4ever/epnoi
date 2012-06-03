package epnoi.model.parameterization;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "inferenceEngine")
public class InferenceEngineParameters {
	int numberOfInteractions;
	float minimumActivation;

	public int getNumberOfInteractions() {
		return numberOfInteractions;
	}

	public void setNumberOfInteractions(int numberOfInteractions) {
		this.numberOfInteractions = numberOfInteractions;
	}

	public float getMinimumActivation() {
		return minimumActivation;
	}

	public void setMinimumActivation(float minimumActivation) {
		this.minimumActivation = minimumActivation;
	}
}
