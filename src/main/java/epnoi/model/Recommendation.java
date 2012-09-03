package epnoi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "recommendation")
public class Recommendation implements Comparable {

	String userURI;
	Float strength;
	Long itemID;
	String itemURI;
	Provenance provenance;
	Explanation explanation;
	String recommenderURI;

	public String getRecommenderURI() {
		return recommenderURI;
	}

	public void setRecommenderURI(String recommenderURI) {
		this.recommenderURI = recommenderURI;
	}

	public Recommendation() {
		this.provenance = new Provenance();
	}

	public Provenance getProvenance() {
		return provenance;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

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

	public int compareTo(Object object) {

		// a negative integer, zero, or a positive integer as this object is
		// less than, equal to, or greater than the specified object.
		// or a ClassCastException
		Recommendation recommendation = (Recommendation) object;
		if (this.strength > recommendation.getStrength())
			return 1;
		else if (this.strength.equals(recommendation.getStrength()))
			return 0;

		return -1;

	}

	@Override
	public String toString() {
		return "R [" + this.strength + " Item (ID|URI)> (" + this.itemID + "|"
				+ this.itemURI + ")";
	}

	public Explanation getExplanation() {
		return explanation;
	}

	public void setExplanation(Explanation explanation) {
		this.explanation = explanation;
	}
}
