package epnoi.inferenceengine;

import java.util.ArrayList;

public class InferenceParameters {
	private ArrayList<Activation> initialActivations;
	private int numberOfIterations;

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
	
	@Override 
	public String toString(){
		return "# of iterations "+numberOfIterations+" Initial Activations:> "+this.initialActivations;
	}

}
