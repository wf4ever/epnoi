package epnoi.inferenceengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InferenceEngine {

	private ExtendedModel extendedModel = null;

	public void init() {

	}

	public InferenceResult perfomInferenceProcess(InferenceParameters parameters){
	
	int numberOfIterations = parameters.getNumberOfIterations();
HashMap<String, ActiveNode> activeNodes = new HashMap<String, ActiveNode>();
HashMap<String, ActiveNode> currentActiveNodes = _initActiveNodes(parameters);
	int inferenceStep =0;
	
	while(!currentActiveNodes.isEmpty() &&inferenceStep<parameters.getNumberOfIterations()){
	 _expandActivationSet(currentActiveNodes, activeNodes);
	 _upadateActivationOfCurrentActiveNodes(currentActiveNodes, activeNodes);
	 _normalizeNodes(currentActiveNodes);
	
	 
	}
	InferenceResult inferenceResults= _generateInferenceResults(activeNodes);
	return inferenceResults;	
}

	/*
	 * Function that initializes the set of current active nodes
	 */
	
	private HashMap<String,ActiveNode> _initActiveNodes(InferenceParameters parameters) {
		HashMap<String, ActiveNode> activeNodes = new HashMap<String, ActiveNode>();
		for (Activation activation : parameters.getInitialActivations()) {
			String URI = activation.getNodeURI();
			Node node = this.extendedModel.getGraph().getNodeByURI(URI);
			ActiveNode activeNode = new ActiveNode(node);
			activeNodes.put(node.getURI(), activeNode);
		}
		return activeNodes;
	}
	/*
	 * Function that expands the 
	 */

	private void _expandActivationSet(
			HashMap<String, ActiveNode> currentActiveNodes,
			HashMap<String, ActiveNode> activeNodes) {
		// All the nodes in the currentActiveNodes set are added to the active
		// nodes
		for (ActiveNode activeNode : currentActiveNodes.values()) {

			activeNodes.put(activeNode.getMirroedNode().getURI(), activeNode);

		}

		// currentActiveNodes=null;
		HashMap<String, ActiveNode> updatedCurrentActiveNodes = new HashMap<String, ActiveNode>();

		// Then we add to the currentActivationNode sets the outgoing
		for (ActiveNode activeNode : currentActiveNodes.values()) {
			ActiveNode destinationActiveNode = null;
			for (Link link : activeNode.getMirroedNode().getOutgoingLinks()) {

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
		}
		currentActiveNodes = null;
		currentActiveNodes = updatedCurrentActiveNodes;
	}

	
	private void _upadateActivationOfCurrentActiveNodes(HashMap<String, ActiveNode> currentActiveNodes, HashMap<String, ActiveNode> activeNodes){
		for (ActiveNode currentActiveNode:currentActiveNodes.values()){
			_upadateActivationOfCurrentActiveNode(currentActiveNode, activeNodes);
		}
	}
	
	private void _upadateActivationOfCurrentActiveNode(ActiveNode currentActiveNode,HashMap<String, ActiveNode> activeNodes){
		float activationUpdate=0;
		for (Link link : currentActiveNode.getMirroedNode().getIncomingLinks()) {
			Node originNode = link.getOrigin();
			float originActivation = 0;
			//If it is already in the active 
			if (activeNodes.containsKey(originNode.getURI())){
				ActiveNode originActiveNode = activeNodes.get(originNode.getURI());
				originActivation=originActiveNode.getActivation();
			}
			 activationUpdate+=originActivation*link.getWeight();
		}
		currentActiveNode.setActivation(currentActiveNode.getActivation()+activationUpdate);
	}
	
	
	private void _normalizeNodes(HashMap<String, ActiveNode> activeNodes){
		
	}
	
	private InferenceResult _generateInferenceResults(HashMap<String, ActiveNode> activeNodes){
		InferenceResult inferenceResults = new InferenceResult();
		
		return inferenceResults;
	}

}
