package epnoi.inferenceengine;

public class Activation {
	String nodeURI;
	float activationValue;
	
	public String getNodeURI() {
		return nodeURI;
	}
	public void setNodeURI(String nodeURI) {
		this.nodeURI = nodeURI;
	}
	public float getActivationValue() {
		return activationValue;
	}
	public void setActivationValue(float activationValue) {
		this.activationValue = activationValue;
	}
	
	@Override
	public String toString(){
		return "A["+this.nodeURI+","+this.activationValue+"]";
	}

}
