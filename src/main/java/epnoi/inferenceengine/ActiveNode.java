package epnoi.inferenceengine;

import epnoi.model.Recommendation;

public class ActiveNode implements Comparable{
	Node mirroedNode;
	float activation;
	
	public Node getMirroedNode() {
		return mirroedNode;
	}

	public void setMirroedNode(Node mirroedNode) {
		this.mirroedNode = mirroedNode;
	}


	public ActiveNode(Node node){
		this.mirroedNode= node;
	}

	public float getActivation() {
		return activation;
	}

	public void setActivation(float activation) {
		this.activation = activation;
	}
	
	public int compareTo(Object object) {

		// a negative integer, zero, or a positive integer as this object is
		// less than, equal to, or greater than the specified object.
		// or a ClassCastException
		ActiveNode recommendation = (ActiveNode) object;
		if (this.activation > recommendation.getActivation())
			return 1;
		if (this.activation == recommendation.getActivation())
			return 0;

		return -1;

	}
	
	@Override
	public String toString(){
		return "AN ["+this.mirroedNode.getURI()+", "+this.activation+"]";
	}
	
}
