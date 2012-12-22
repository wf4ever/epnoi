package epnoi.model;

import java.util.HashMap;

public class ContextModel {
	private HashMap<String, RecommendationContext> recommendationContexts;
	private HashMap<String, ActionsContext> actionsContexts;

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public ContextModel() {
		this.recommendationContexts = new HashMap<String, RecommendationContext>();
		this.actionsContexts= new HashMap<String, ActionsContext>();
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------

	public void addRecommendationContext(String userURI,
			RecommendationContext recommendationContext) {
		this.recommendationContexts.put(userURI, recommendationContext);
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------------

	public RecommendationContext getUserRecommendationContext(String userURI) {
		return this.recommendationContexts.get(userURI);
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void addAction(String userURI, Action action) {
		ActionsContext userActionsContext = this.actionsContexts.get(userURI);
		if (userActionsContext==null){
			userActionsContext = new ActionsContext();
		}
		userActionsContext.addAction(action);
		this.actionsContexts.put(userURI, userActionsContext);
		System.out.println("----------->>>"+this.actionsContexts);
		
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public ActionsContext getActionsContext(String userURI) {
		return this.actionsContexts.get(userURI);
	}

}
