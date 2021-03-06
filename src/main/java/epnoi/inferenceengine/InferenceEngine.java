package epnoi.inferenceengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import epnoi.model.Explanation;
import epnoi.model.Model;
import epnoi.model.Parameter;
import epnoi.model.Provenance;
import epnoi.model.Recommendation;
import epnoi.model.RecommendationSpace;
import epnoi.model.User;
import epnoi.model.parameterization.ParametersModel;

public class InferenceEngine {
	/**

	 */

	private ExtendedModel extendedModel = null;
	private ParametersModel parametersModel = null;

	public InferenceEngine() {

	}

	public InferenceEngine(ParametersModel parametersModel) {
		this.parametersModel = parametersModel;
	}

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
		/*
		 * System.out.println(); System.out.println();
		 * 
		 * System.out.println(
		 * "-----------------------------------------------------------------------"
		 * ); System.out.println("Inference parameters "+parameters);
		 * System.out.println("Activation Constant " + conservationConstant);
		 */

		while (!currentActiveNodes.isEmpty()
				&& inferenceStep < numberOfIterations) {
			/*
			 * System.out.println("Inference Engine (step " + inferenceStep +
			 * ")-------------------------");
			 * System.out.println("A)Curren active nodes> " +
			 * currentActiveNodes); System.out.println("A)Active nodes> " +
			 * activeNodes);
			 */
			// For each step of the inference process
			// A) We expand the
			this._expandActivationSet(currentActiveNodes, activeNodes,
					inferenceStep);
			// B) We update the activation of the nodes
			this._upadateActivationOfCurrentActiveNodes(currentActiveNodes,
					activeNodes, inferenceStep);
			// C) We normalize the activation value of the nodes
			this._normalizeNodes(activeNodes, conservationConstant);
			// D) We filter those nodes following certain policies
			_filterNodes(currentActiveNodes);
			// E) We increase the algorithm step
			inferenceStep++;
			/*
			 * System.out.println("B)Curren active nodes> " +
			 * currentActiveNodes); System.out.println("B)Active nodes> " +
			 * activeNodes);
			 */
		}
		InferenceResult inferenceResults = _generateInferenceResults(
				activeNodes, conservationConstant);
		_fitlerInitiallyActivated(inferenceResults, parameters);
		return inferenceResults;
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param inferenceResult
	 * @param inferenceParameters
	 */
	private void _fitlerInitiallyActivated(InferenceResult inferenceResult,
			InferenceParameters inferenceParameters) {

		Set<String> initiallyActiveNodes = new HashSet<String>();
		for (Activation activation : inferenceParameters
				.getInitialActivations()) {
			initiallyActiveNodes.add(activation.getNodeURI());
		}
		ArrayList<Activation> activationsToRemove = new ArrayList<Activation>();
		for (Activation activation : inferenceResult.getActivations()) {
			if (initiallyActiveNodes.contains(activation.getNodeURI())) {
				activationsToRemove.add(activation);
			}
		}
		for (Activation activation : activationsToRemove) {
			inferenceResult.getActivations().remove(activation);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	/*
	 * Function that initializes the set of current active nodes
	 */

	private HashMap<String, ActiveNode> _initActiveNodes(
			InferenceParameters parameters) {
		HashMap<String, ActiveNode> activeNodes = new HashMap<String, ActiveNode>();
		for (Activation activation : parameters.getInitialActivations()) {
			String URI = activation.getNodeURI();
			Node node = this.extendedModel.getGraph().getNodeByURI(URI);

			if (node != null) {
				ActiveNode activeNode = new ActiveNode(node);
				activeNode.setActivation(activation.getActivationValue());

				activeNodes.put(node.getURI(), activeNode);
			}
		}
		return activeNodes;
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	/*
	 * Function that expands the activation set
	 */

	private void _expandActivationSet(
			HashMap<String, ActiveNode> currentActiveNodes,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		// All the nodes in the currentActiveNodes set are added to the active
		// nodes

		for (ActiveNode activeNode : currentActiveNodes.values()) {

			activeNodes.put(activeNode.getMirroedNode().getURI(), activeNode);
			// In the case of the first inference step we
			/*
			 * if (inferenceStep == 0) { activeNode.setActivation(0f); }
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
		/*
		 * System.out.println("Estos son los updatedCurrentActiveNodes " +
		 * updatedCurrentActiveNodes);
		 */
		currentActiveNodes.putAll(updatedCurrentActiveNodes);
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private void _upadateActivationOfCurrentActiveNodes(
			HashMap<String, ActiveNode> currentActiveNodes,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		for (ActiveNode currentActiveNode : currentActiveNodes.values()) {
			_upadateActivationOfCurrentActiveNode(currentActiveNode,
					activeNodes, inferenceStep);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private void _upadateActivationOfCurrentActiveNode(
			ActiveNode currentActiveNode,
			HashMap<String, ActiveNode> activeNodes, int inferenceStep) {
		float activationUpdate = 0;
		for (Link link : currentActiveNode.getMirroedNode().getIncomingLinks()) {

			Node originNode = link.getOrigin();
			// System.out.println("origNode > " + originNode);
			float originActivation = 0;
			// If it is already in the active nodes, we need to retrieve the
			// node
			if (activeNodes.containsKey(originNode.getURI())) {

				ActiveNode originActiveNode = activeNodes.get(originNode
						.getURI());

				// System.out.println("It was activated " + activationUpdate +
				// " and it was " + originActiveNode);
				originActivation = originActiveNode.getActivation();
				if (inferenceStep == 0) {
					originActiveNode.setActivation(0f);
				}
			}

			activationUpdate += originActivation * link.getWeight();
		}
		// System.out.println("The activation update is " + activationUpdate);
		currentActiveNode.setActivation(currentActiveNode.getActivation()
				+ activationUpdate);
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private void _normalizeNodes(HashMap<String, ActiveNode> activeNodes,
			float conservationConstant) {
		float actualActivationTotal = this
				._calculateTotalActivation(activeNodes);

		for (ActiveNode activeNode : activeNodes.values()) {
			float currentActivation = activeNode.getActivation();
			float proportionOfActualTotal = currentActivation
					/ actualActivationTotal;
			activeNode.setActivation(proportionOfActualTotal
					* conservationConstant);

		}

	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private void _filterNodes(HashMap<String, ActiveNode> activeNodes) {

	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private InferenceResult _generateInferenceResults(
			HashMap<String, ActiveNode> activeNodes, float conservationConstant) {
		InferenceResult inferenceResults = new InferenceResult();
		ArrayList<ActiveNode> orderedActiveNodes = new ArrayList<ActiveNode>(
				activeNodes.values());
		Collections.sort(orderedActiveNodes);
		Collections.reverse(orderedActiveNodes);
		this._normalizeAndFilter(orderedActiveNodes, conservationConstant);
		ArrayList<Activation> activations = new ArrayList<Activation>();
		for (ActiveNode activeNode : orderedActiveNodes) {
			Activation activation = new Activation();
			activation.setNodeURI(activeNode.getMirroedNode().getURI());
			activation.setActivationValue(activeNode.getActivation());
			activations.add(activation);
		}
		inferenceResults.setActivations(activations);
		return inferenceResults;
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private float _calculateTotalActivation(
			HashMap<String, ActiveNode> activeNodes) {
		float activationConstant = 0f;
		for (ActiveNode activeNode : activeNodes.values()) {
			activationConstant += activeNode.getActivation();
		}
		return activationConstant;
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private void _normalizeAndFilter(ArrayList<ActiveNode> activeNodes,
			float conservationConstant) {
		for (ActiveNode activeNode : activeNodes) {
			activeNode.setActivation(activeNode.getActivation()
					/ conservationConstant);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	public RecommendationSpace infer(RecommendationSpace recommendationSpace) {
		RecommendationSpace inferredRecommendationSpace = new RecommendationSpace();
		float minimumActivation = this.parametersModel.getInferenceEngine()
				.getMinimumActivation();
		for (User user : this.extendedModel.getModel().getUsers()) {
			ArrayList<Recommendation> recommendationsForUser = recommendationSpace
					.getRecommendationsForUserURI(user.getURI());
			// If the user has been provided with any recommendation, we try to
			// infer new ones
			if (recommendationsForUser.size() > 0) {

				InferenceParameters inferenceParameters = new InferenceParameters();
				inferenceParameters.setNumberOfIterations(this.parametersModel
						.getInferenceEngine().getNumberOfInteractions());
				inferenceParameters.setMinimumActivation(this.parametersModel
						.getInferenceEngine().getMinimumActivation());
				ArrayList<Activation> initialActivations = _generateActivation(recommendationsForUser);
				// System.out.println(":.......>  " + initialActivations);
				inferenceParameters.setInitialActivations(initialActivations);
				InferenceResult inferenceResult = this
						.perfomInferenceProcess(inferenceParameters);

				// System.out.println("The result for user " + user.getURI()+
				// " is " + inferenceResult.getActivations());

				for (Activation activation : inferenceResult.getActivations()) {

					if (activation.getActivationValue() > minimumActivation) {
						// System.out.println(">> URI >"+user.getURI());
						Recommendation recommendation = new Recommendation();
						recommendation.setUserURI(user.getURI());
						recommendation.setStrength(activation
								.getActivationValue()*5);
						recommendation.setItemURI(activation.getNodeURI());
						recommendation.setRecommenderURI("InferenceEngine");
						Parameter parameterTechnique = new Parameter();
						parameterTechnique.setName(Provenance.TECHNIQUE);
						parameterTechnique
								.setValue(Provenance.TECHNIQUE_INFERRED);

						Parameter parameter = new Parameter();
						parameter.setName(Provenance.ITEM_TYPE);
						parameter.setValue(Provenance.ITEM_TYPE_PACK);

						Explanation explanation = new Explanation();

						explanation
								.setExplanation("The pack is recommended to you since it contains items that either you own or have been recommended to you");
						explanation.setTimestamp(new Date(System
								.currentTimeMillis()));
						recommendation.setExplanation(explanation);

						recommendation.getProvenance().getParameters()
								.add(parameter);
						inferredRecommendationSpace.addRecommendationForUser(
								user, recommendation);
					}
				}

				
			}
		}
		return inferredRecommendationSpace;
	}
	
	// --------------------------------------------------------------------------------------------------------------------

	private ArrayList<Activation> _generateActivation(
			ArrayList<Recommendation> recommendationsForUser) {
		ArrayList<Activation> initialActivations = new ArrayList<Activation>();
		for (Recommendation recommenadtionForUser : recommendationsForUser) {
			recommenadtionForUser.getItemURI();

			Activation activation = new Activation();
			activation.setNodeURI(recommenadtionForUser.getItemURI());
			// As recommendations strength range from 0 to 5 we divide its value
			// to change the range
			// to [0, 1]
			activation
					.setActivationValue(recommenadtionForUser.getStrength() / 5);
			initialActivations.add(activation);
		}
		return initialActivations;
	}

}
