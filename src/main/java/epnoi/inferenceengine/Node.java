package epnoi.inferenceengine;

import java.util.ArrayList;

public class Node {
	private String URI;
	
	private ArrayList<Link> outgoingLinks;
	private ArrayList<Link> incomingLinks;
	
	public ArrayList<Link> getIncomingLinks() {
		return incomingLinks;
	}

	public void setIncomingLinks(ArrayList<Link> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	public Node(){
		this.outgoingLinks=new ArrayList<Link>();
		this.incomingLinks=new ArrayList<Link>();
	}

	public int getFanout() {
		return outgoingLinks.size();
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}
	
	public void addOutgoingLink(Link link){
		this.outgoingLinks.add(link);
	}

	public ArrayList<Link> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(ArrayList<Link> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

}
