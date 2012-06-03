package epnoi.inferenceengine;

import java.util.ArrayList;

public class InferenceParameters {
	private ArrayList<Activation> initialActivations;
	private int numberOfIterations;
	private float minimumActivation;

	public int getNumberOfIterations() {
		return numberOfIterations;
	}

	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	public ArrayList<Activation> getInitialActivations() {
		return initialActivations;
	}

	public void setInitialActivations(ArrayList<Activation> initialActivations) {
		this.initialActivations = initialActivations;
	}

	public float getMinimumActivation() {
		return minimumActivation;
	}

	public void setMinimumActivation(float minimumActivation) {
		this.minimumActivation = minimumActivation;
	}
	
	@Override
	public String toString() {
		return "# of iterations " + numberOfIterations
				+ " Initial Activations:> " + this.initialActivations;
	}

}
