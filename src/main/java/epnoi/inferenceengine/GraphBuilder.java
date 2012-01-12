package epnoi.inferenceengine;

import epnoi.model.Model;

public class GraphBuilder {
 public static Graph buildGraph(){
	 Graph newGraph = new Graph();
	 Node nodeA = newGraph.createNode("NodeA");
	 Node nodeB = newGraph.createNode("NodeB");
	 Node nodeC = newGraph.createNode("NodeC");
	 Link linkAB = newGraph.createLink("LinkAB");
	 Link linkBC = newGraph.createLink("LinkBC");
	 
	 return newGraph;
	 
 }
 
 public static Graph buildGraph(Model model){
	 Graph graph = new Graph();
	 return graph;
 }
}
