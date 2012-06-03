package epnoi.inferenceengine;

import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Pack;
import epnoi.model.Workflow;

public class Wf4EverGraphBuilder {
	/*
	 * public static Graph buildGraph(){ Graph newGraph = new Graph(); Node
	 * nodeA = newGraph.createNode("NodeA"); Node nodeB =
	 * newGraph.createNode("NodeB"); Node nodeC = newGraph.createNode("NodeC");
	 * Node nodeD = newGraph.createNode("NodeD");
	 * 
	 * Link linkAC = newGraph.createLink("LinkAC"); linkAC.setWeight(0.5f);
	 * newGraph.connect(nodeA, nodeC, linkAC);
	 * 
	 * 
	 * Link linkBC = newGraph.createLink("LinkBC"); linkBC.setWeight(0.25f);
	 * newGraph.connect(nodeB, nodeC, linkBC);
	 * 
	 * 
	 * Link linkCD = newGraph.createLink("LinkCD"); linkCD.setWeight(1f);
	 * newGraph.connect(nodeC, nodeD, linkCD);
	 * 
	 * Link linkDA = newGraph.createLink("LinkDA"); linkDA.setWeight(1f);
	 * newGraph.connect(nodeD, nodeA, linkDA);
	 * 
	 * return newGraph;
	 * 
	 * }
	 */
	public static Graph buildGraph(Model model) {
		Graph newGraph = new Graph();
		// We need to add to the graph all the relevant elements in the model
		// Workflows

		// System.out.println(">>>>>>>>>>>>>>"+model.getWorkflowByURI("http://www.myexperiment.org/workflow.xml?id=2049"));

		for (Workflow workflow : model.getWorkflows()) {
			/*
			 * if (workflow.getURI().equals(
			 * "http://www.myexperiment.org/workflow.xml?id=2049")){
			 * System.out.println
			 * ("EEEEEEEEEEE--------------------------------------STA"); }
			 */
			// System.out.println(">> "+workflow.getURI());
			Node nodeA = newGraph.createNode(workflow.getURI());
		}

		// Files
		for (File file : model.getFiles()) {
			Node nodeA = newGraph.createNode(file.getURI());
		}

		for (Pack pack : model.getPacks()) {
			Node packNode = newGraph.createNode(pack.getURI());

			for (String fileURI : pack.getInternalFiles()) {
				Node fileNode = newGraph.getNodeByURI(fileURI);
				if (fileNode != null) {
					Link newLink = newGraph
							.createLink("http://www.openarchives.org/ore/terms/isAggregatedBy");
					newLink.setWeight(0.8f);
					newGraph.connect(fileNode, packNode, newLink);
				} else {
					// System.out.println("AQUI                                "+fileURI);
				}
			}
			// Addition of the ore:isAggregatedBy
			// ore->http://www.openarchives.org/ore/terms/
			for (String workflowURI : pack.getInternalWorkflows()) {
				Node workflowNode = newGraph.getNodeByURI(workflowURI);
				if (workflowNode != null) {
					Link newLink = newGraph
							.createLink("http://www.openarchives.org/ore/terms/isAggregatedBy");
					newLink.setWeight(0.8f);
					newGraph.connect(workflowNode, packNode, newLink);
				} else {
					// System.out.println("AQUI                                "+workflowURI);
				}

			}
		}

		return newGraph;
	}

}
