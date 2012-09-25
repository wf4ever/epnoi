package epnoi.model;

import java.util.HashMap;

public class ContextModel {
	private HashMap<String, RecommendationContext> recommendationContexts;

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ContextModel() {
		this.recommendationContexts = new HashMap<String, RecommendationContext>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void addContext(String userURI,
			RecommendationContext recommendationContext) {
		this.recommendationContexts.put(userURI, recommendationContext);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------

	public RecommendationContext getUserContext(String userURI) {
		return this.recommendationContexts.get(userURI);
	}

}
