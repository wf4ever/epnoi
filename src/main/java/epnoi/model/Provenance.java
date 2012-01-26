package epnoi.model;

import java.util.ArrayList;
import java.util.Iterator;

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

	public String getParameterByName(String name) {
		boolean found = false;
		String parameterValue = null;
		Iterator<Parameter> parametersIt = this.parameters.iterator();
		while (parametersIt.hasNext()) {
			Parameter parameter = parametersIt.next();
			found = parameter.getName().equals(name);
			if (found) {
				parameterValue = parameter.getValue();
			}
		}
		return parameterValue;
	}
}
