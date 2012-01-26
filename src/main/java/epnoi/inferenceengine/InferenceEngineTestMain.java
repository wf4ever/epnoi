package epnoi.inferenceengine;

import java.util.ArrayList;

import epnoi.model.Model;

public class InferenceEngineTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("IN> Test of the inference engine!!!");
		InferenceEngine inferenceEngine = new InferenceEngine();
		Model emptyModel = null;
		inferenceEngine.init(emptyModel);
		InferenceParameters inferenceParameters = new InferenceParameters();
		inferenceParameters.setNumberOfIterations(50);
		ArrayList<Activation> initialActiveNodes = new ArrayList<Activation>();
		Activation activationA = new Activation();
		activationA.setActivationValue(1.0f);
		activationA.setNodeURI("NodeA");
		
		Activation activationB = new Activation();
		activationB.setActivationValue(0.5f);
		activationB.setNodeURI("NodeB");
		
		initialActiveNodes.add(activationA);
		initialActiveNodes.add(activationB);
		inferenceParameters.setInitialActivations(initialActiveNodes);

		
		InferenceResult inferenceResult = inferenceEngine.perfomInferenceProcess(inferenceParameters);
		System.out.println(inferenceResult.getActivations());
		
		
		
		System.out.println("OUT> Test of the inference engine!!!");
		

	}

}
