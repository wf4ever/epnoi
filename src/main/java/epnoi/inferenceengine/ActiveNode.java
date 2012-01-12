package epnoi.inferenceengine;

public class ActiveNode {
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
	
}
