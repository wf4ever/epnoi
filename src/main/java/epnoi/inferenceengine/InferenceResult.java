package epnoi.inferenceengine;

import java.util.ArrayList;

public class InferenceResult {
	private ArrayList<Activation> resultActivations;

	public ArrayList<Activation> getActivations() {
		return resultActivations;
	}

	public void setActivations(ArrayList<Activation> activations) {
		this.resultActivations = activations;
	}

}
