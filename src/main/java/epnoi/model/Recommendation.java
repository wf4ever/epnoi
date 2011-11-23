package epnoi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recommendation")
public class Recommendation {

	String userURI;
	Float strength;
	Long itemID;
	String itemURI;

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}

	public Float getStrength() {
		return strength;
	}

	public void setStrength(Float strenght) {
		this.strength = strenght;
	}

	public Long getItemID() {
		return itemID;
	}

	public void setItemID(Long itemID) {
		this.itemID = itemID;
	}

	public String getItemURI() {
		return itemURI;
	}

	public void setItemURI(String itemURI) {
		this.itemURI = itemURI;
	}
}
