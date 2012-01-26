package epnoi.inferenceengine;

import epnoi.model.Model;

public class GraphBuilder {
 public static Graph buildGraph(){
	 Graph newGraph = new Graph();
	 Node nodeA = newGraph.createNode("NodeA");
	 Node nodeB = newGraph.createNode("NodeB");
	 Node nodeC = newGraph.createNode("NodeC");
	 Node nodeD = newGraph.createNode("NodeD");
	 
	 Link linkAC = newGraph.createLink("LinkAC");
	 linkAC.setWeight(0.5f);
	 newGraph.connect(nodeA, nodeC, linkAC);
	 
	 
	 Link linkBC = newGraph.createLink("LinkBC");
	 linkBC.setWeight(0.25f);
	 newGraph.connect(nodeB, nodeC, linkBC);
	 
	 
	 Link linkCD = newGraph.createLink("LinkCD");
	 linkCD.setWeight(1f);
	 newGraph.connect(nodeC, nodeD, linkCD);
	 
	 Link linkDA = newGraph.createLink("LinkDA");
	 linkDA.setWeight(1f);
	 newGraph.connect(nodeD, nodeA, linkDA);
	 
	 return newGraph;
	 
 }
 
 public static Graph buildGraph(Model model){
	return Wf4EverGraphBuilder.buildGraph(model);
 }
}
