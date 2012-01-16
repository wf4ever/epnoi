package epnoi.inferenceengine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import epnoi.model.Model;

public class InferenceEngine {
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	private ExtendedModel extendedModel = null;

	public void init(Model model) {
		this.extendedModel = ExtendedModelBuilder.buildExtendedModel(model);
	}

	public ExtendedModel getExtendedModel() {
		return extendedModel;
	}

	public void setExtendedModel(ExtendedModel extendedModel) {
		this.extendedModel = extendedModel;
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */

	public InferenceResult perfomInferenceProcess(InferenceParameters parameters) {

		int numberOfIterations = parameters.getNumberOfIterations();
		HashMap<String, ActiveNode> activeNodes = new HashMap<String, ActiveNode>();
		HashMap<String, ActiveNode> currentActiveNodes = _initActiveNodes(parameters);
		int inferenceStep = 0;
		float conservationConstant = this
				._calculateTotalActivation(currentActiveNodes);
		System.out.println("Activation Constant " + conservationConstant);

		while (!currentActiveNodes.isEmpty()
				&& inferenceStep < numberOfIterations) {
			System.out.println("Inference Engine (step " + inferenceStep
					+ ")-------------------------");
			System.out.println("A)Curren active nodes> " + currentActiveNodes);
			System.out.println("A)Active nodes> " + activeNodes);

			// For each step of the inference process
			// A) We expand the
			this._expandActivationSet(currentActiveNodes, activeNodes, inferenceStep);
			// B) We update the activation of the nodes
			this._upadateActivationOfCurrentActiveNodes(currentActiveNodes,
					activeNodes, inferenceStep);
			// C) We normalize the activation value of the nodes
			this._normalizeNodes(activeNodes, conservationConstant);
			// D) We filter those nodes following certain policies
			_filterNodes(currentActiveNodes);
			// E) We increase the algorithm step
			inferenceStep++;
			System.out.println("B)Curren active nodes> " + currentActiveNodes);
			System.out.println("B)Active nodes> " + activeNodes);

		}
		InferenceResult inferenceResults = _generateInferenceResults(activeNodes, conservationConstant);
		return inferenceResults;
	}

	/*
	 * Function that initializes the set of current active nodes
	 */

	private HashMap<String, ActiveNode> _initActiveNodes(
			InferenceParameters parameters) {
		HashMap<String, ActiveNode> activeNodes = new HashMap<String, ActiveNode>();
		for (Activation activation : parameters.getInitialActivations()) {
			String URI = activation.getNodeURI();
			Node node = this.extendedModel.getGraph().getNodeByURI(URI);
			ActiveNode activeNode = new ActiveNode(node);
			activeNode.setActivation(activation.getActivationValue());
			activeNodes.put(node.getURI(), activeNode);
		}
		return activeNodes;
	}

	/*
	 * Function that expands the
	 */

	private void _expandActivationSet(
			HashMap<String, ActiveNode> currentActiveNodes,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		// All the nodes in the currentActiveNodes set are added to the active
		// nodes

		for (ActiveNode activeNode : currentActiveNodes.values()) {

			activeNodes.put(activeNode.getMirroedNode().getURI(), activeNode);
			//In the case of the first inference step we 
			/*
			if (inferenceStep == 0) {
				activeNode.setActivation(0f);
			}
			*/
		}

		// currentActiveNodes=null;
		HashMap<String, ActiveNode> updatedCurrentActiveNodes = new HashMap<String, ActiveNode>();

		// Then we add to the currentActivationNode sets the outgoing
		HashMap<String, ActiveNode> currentActiveNodesAux = (HashMap<String, ActiveNode>) currentActiveNodes
				.clone();

		for (ActiveNode activeNode : currentActiveNodesAux.values()) {

			for (Link link : activeNode.getMirroedNode().getOutgoingLinks()) {
				ActiveNode destinationActiveNode = null;
				// System.out.println(":>>>" + link.getDestination());
				if (activeNodes.containsKey((link.getDestination().getURI()))) {
					destinationActiveNode = activeNodes.get(link
							.getDestination().getURI());

				} else {
					destinationActiveNode = new ActiveNode(
							link.getDestination());
					activeNodes.put(destinationActiveNode.getMirroedNode()
							.getURI(), destinationActiveNode);

				}
				updatedCurrentActiveNodes.put(destinationActiveNode
						.getMirroedNode().getURI(), destinationActiveNode);
			}
			currentActiveNodes.remove(activeNode.getMirroedNode().getURI());
		}

		System.out.println("Estos son los updatedCurrentActiveNodes "
				+ updatedCurrentActiveNodes);
		currentActiveNodes.putAll(updatedCurrentActiveNodes);
	}

	private void _upadateActivationOfCurrentActiveNodes(
			HashMap<String, ActiveNode> currentActiveNodes,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		for (ActiveNode currentActiveNode : currentActiveNodes.values()) {
			_upadateActivationOfCurrentActiveNode(currentActiveNode,
					activeNodes, inferenceStep);
		}
	}

	private void _upadateActivationOfCurrentActiveNode(
			ActiveNode currentActiveNode,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		float activationUpdate = 0;
		for (Link link : currentActiveNode.getMirroedNode().getIncomingLinks()) {

			Node originNode = link.getOrigin();
			System.out.println("origNode > " + originNode);
			float originActivation = 0;
			// If it is already in the active nodes, we need to retrieve the
			// node
			if (activeNodes.containsKey(originNode.getURI())) {

				ActiveNode originActiveNode = activeNodes.get(originNode
						.getURI());
				System.out.println("It was activated " + activationUpdate
						+ " and it was " + originActiveNode);
				originActivation = originActiveNode.getActivation();
				if (inferenceStep==0){
					originActiveNode.setActivation(0f);
				}
			}

			activationUpdate += originActivation * link.getWeight();
		}
		System.out.println("The activation update is " + activationUpdate);
		currentActiveNode.setActivation(currentActiveNode.getActivation()
				+ activationUpdate);
	}

	private void _normalizeNodes(HashMap<String, ActiveNode> activeNodes,
			float conservationConstant) {
		float actualActivationTotal = this._calculateTotalActivation(activeNodes);
		
		for (ActiveNode activeNode:activeNodes.values()){
			float currentActivation = activeNode.getActivation();
			float proportionOfActualTotal = currentActivation/actualActivationTotal;
			activeNode.setActivation(proportionOfActualTotal*conservationConstant);
			
		}

	}

	private void _filterNodes(HashMap<String, ActiveNode> activeNodes) {

	}

	private InferenceResult _generateInferenceResults(
			HashMap<String, ActiveNode> activeNodes, float conservationConstant) {
		InferenceResult inferenceResults = new InferenceResult();
		ArrayList<ActiveNode> orderedActiveNodes = new ArrayList<ActiveNode>(
				activeNodes.values());
		Collections.sort(orderedActiveNodes);
		Collections.reverse(orderedActiveNodes);
		this._normalizeAndFilter(orderedActiveNodes, conservationConstant);
		inferenceResults.setActiveNodes(orderedActiveNodes);
		return inferenceResults;
	}

	private float _calculateTotalActivation(
			HashMap<String, ActiveNode> activeNodes) {
		float activationConstant = 0f;
		for (ActiveNode activeNode : activeNodes.values()) {
			activationConstant += activeNode.getActivation();
		}
		return activationConstant;
	}
	
	private void _normalizeAndFilter(ArrayList<ActiveNode> activeNodes, float conservationConstant){
		for (ActiveNode activeNode: activeNodes){
			activeNode.setActivation(activeNode.getActivation()/conservationConstant);
		}
	}

}
