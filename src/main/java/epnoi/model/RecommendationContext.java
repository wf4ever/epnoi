package epnoi.model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecommendationContext {
	public static final String PACK_URI = "PACK_URI";
	String userURI;
	List<String> keyword;
	List<String> resource;
	List<Parameter> parameters;

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public List<String> getKeyword() {
		return keyword;
	}

	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}

	public List<String> getResource() {
		return resource;
	}

	public void setResource(List<String> resource) {
		this.resource = resource;
	}

	
	public List<Parameter> getParameters() {
		return parameters;
	}

	// ----------------------------------------------------------------------------------------
	
	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	// ----------------------------------------------------------------------------------------

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
	@Override
	public String toString() {
		return "RC [User " + this.userURI + " | keyword "+this.keyword+ " | resource "+this.resource+" ]";
	}
}
