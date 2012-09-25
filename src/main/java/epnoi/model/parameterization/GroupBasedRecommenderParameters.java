package epnoi.model.parameterization;

public class GroupBasedRecommenderParameters extends RecommenderParameters {
	String indexPath;
	Integer numberOfQueryHits;

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
		return "Group Based Recommender[#QueryHits:"+this.numberOfQueryHits+", IndexPath"+this.indexPath+"]";
	}
}
