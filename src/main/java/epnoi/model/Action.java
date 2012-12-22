package epnoi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Action {
	String timestamp;
	 String name;
	 String itemURI;
	

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemURI() {
		return itemURI;
	}

	public void setItemURI(String itemURI) {
		this.itemURI = itemURI;
	}
	
	@Override
	public String toString(){
		return "A[name> "+this.name+", itemURI> "+this.itemURI+", timestamp> "+this.timestamp+ "]";
	}

}
