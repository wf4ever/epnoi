package epnoi.recommeders;



public class UserSimilarityValue implements Comparable {

	String userURI;
	Double similarity;
	
	public Double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}

	@Override
	public String toString() {
		return "US["+this.userURI+"|" + this.similarity + "]";
	}

	public int compareTo(Object object) {

		// a negative integer, zero, or a positive integer as this object is
		// less than, equal to, or greater than the specified object.
		// or a ClassCastException
		UserSimilarityValue recommendation = (UserSimilarityValue) object;
		if (this.similarity > recommendation.getSimilarity())
			return 1;
		if (this.similarity == recommendation.getSimilarity())
			return 0;

		return -1;

	}

	public String getUserURI() {
		return userURI;
	}

	public void setUserURI(String userURI) {
		this.userURI = userURI;
	}


}

