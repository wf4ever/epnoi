package epnoi.inferenceengine;

import java.util.ArrayList;
import java.util.HashMap;

import epnoi.model.File;
import epnoi.model.User;
import epnoi.model.Workflow;

public class Graph {
	private ArrayList<Node> nodes;
	private ArrayList<Link> links;
	private HashMap<String, Node> nodesByURI;
	
	
	public Graph(){
		this.nodes = new ArrayList<Node>();
		this.links = new ArrayList<Link>();
		this.nodesByURI = new HashMap<String, Node>();
	}

	public Node createNode(String URI){
		Node node = new Node();
		node.setURI(URI);
		this.nodesByURI.put(URI, node);
		return node;
		
	}
	public Link createLink(String URI){
		Link newLink = new Link();
		return newLink;
		
	}
	
	public Node getNodeByURI(String URI) {
		return this.nodesByURI.get(URI);
	}
	
	
	public void connect(Node originNode, Node destinationNode, Link link){
		originNode.getOutgoingLinks().add(link);
		destinationNode.getIncomingLinks().add(link);
		link.setOrigin(originNode);
		link.setDestination(destinationNode);
	}
	
}
