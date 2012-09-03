package epnoi.model.parameterization;

public class KeywordRecommenderParameters extends RecommenderParameters {
	String indexPath;
	Integer numberOfQueryHits; //Harcoded to 10 

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public Integer getNumberOfQueryHits() {
		return numberOfQueryHits;
	}

	public void setNumberOfQueryHits(Integer numberOfQueryHits) {
		this.numberOfQueryHits = numberOfQueryHits;
	}
	
	@Override
	public String toString(){
		return "Keyword Recommender[#QueryHits:"+this.numberOfQueryHits+", IndexPath"+this.indexPath+"]";
	}
}
