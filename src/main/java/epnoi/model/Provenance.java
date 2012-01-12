package epnoi.model;

import java.util.ArrayList;

public class Provenance {
	private ArrayList<Parameter> parameters;

	public Provenance() {
		this.parameters = new ArrayList<Parameter>();
	}

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}
}
