package epnoi.model;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecommendationContext {
	String userURI;
	List<String> keyword;
	List<String> resource;
	

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

	@Override
	public String toString() {
		return "RC [User " + this.userURI + " | keyword "+this.keyword+ " | resource "+this.resource+" ]";
	}
}
