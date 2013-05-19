package epnoi.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Search implements Resource {

	private String URI;
	private String title;
	private String description;
	ArrayList<String> expressions;
	
	public Search(){
		this.expressions=new ArrayList<String>();
	}

	public ArrayList<String> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<String> expressions) {
		this.expressions = expressions;
	}
	
	public void addExpression(String expression){
		this.expressions.add(expression);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getURI() {
		return this.URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
