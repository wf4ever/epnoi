package epnoi.core;

import epnoi.logging.EpnoiLogger;
import epnoi.model.Recommendation;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelReader;

public class EpnoiCoreMain {

	public static void main(String[] args) {
		EpnoiLogger.setup("");
		System.out.println("Starting the EpnoiCoreMain");
		EpnoiCore epnoiCore = new EpnoiCore();

		ParametersModel parametersModel = ParametersModelReader
				.read("/parametersModelPath.xml");

		epnoiCore.init(parametersModel);
		/*
		 * for (User user : epnoiCore.getModel().getUsers()) { if
		 * ((epnoiCore.getRecommendationSpace()
		 * .getRecommendationsForUserURI(user.getURI())).size() > 0) {
		 * System.out.println("Recommedations for user " + user.getName() +
		 * "--------------------------------"); for (Recommendation
		 * recommendation : epnoiCore .getRecommendationSpace()
		 * .getRecommendationsForUserURI(user.getURI())) { //
		 * System.out.println("Recommendation for " // +
		 * recommendation.getUserURI());
		 * 
		 * System.out.println("         (i)" + recommendation.getItemID());
		 * System.out.println("         (s)" + recommendation.getStrength());
		 * System.out.println("         (i.URI)" + recommendation.getItemURI());
		 * 
		 * } }
		 * 
		 * 
		 * }
		 */
		for (Recommendation recommendation : epnoiCore
				.getInferredRecommendationSpace().getAllRecommendations()) {
			System.out.println("-------------------------------------------");
			System.out.println("--->(ItemURI)> " + recommendation.getItemURI());
			System.out.println("--->(Strength)> "
					+ recommendation.getStrength());
			System.out.println("--->(Strength)> "
					+ recommendation.getExplanation().getExplanation());

		}

		epnoiCore.close();
	}

}
