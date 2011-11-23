package epnoi.model;

public class Tagging implements Comparable {

	String tag;
	int numberOfTaggings;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getNumberOfTaggings() {
		return numberOfTaggings;
	}

	public void setNumberOfTaggings(int numberOfTaggings) {
		this.numberOfTaggings = numberOfTaggings;
	}

	public int compareTo(Object object) {

		// a negative integer, zero, or a positive integer as this object is
		// less than, equal to, or greater than the specified object.
		// or a ClassCastException
		return (this.numberOfTaggings - ((Tagging) object)
				.getNumberOfTaggings());
	}
}
