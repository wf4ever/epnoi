package epnoi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import epnoi.model.Rating;
import epnoi.model.Recommendation;
import epnoi.model.Tagging;
import epnoi.model.User;
import epnoi.model.parameterization.ParametersModel;
import epnoi.model.parameterization.ParametersModelWrapper;
import epnoi.recommeders.Recommender;

public class EpnoiCoreMain {

	public static void main(String[] args) {
		System.out.println("Starting the EpnoiCoreMain");
		EpnoiCore epnoiCore = new EpnoiCore();

		ParametersModel parametersModel = ParametersModelWrapper
				.read("/parametersModelPath.xml");

		/*
		 * Properties initializationProperties = new Properties();
		 * 
		 * initializationProperties.setProperty(EpnoiCore.INDEX_PATH_PROPERTY,
		 * "/wf4ever/indexMyExperiment");
		 * initializationProperties.setProperty(EpnoiCore.MODEL_PATH_PROPERTY,
		 * "/wf4ever/lastImportedModel.xml");
		 */
		epnoiCore.init(parametersModel);

		for (User user : epnoiCore.getModel().getUsers()) {
			if ((epnoiCore.getInferredRecommendationSpace()
					.getRecommendationsForUserURI(user.getURI())).size() > 0) {
				System.out.println("Recommedations for user " + user.getName()
						+ "--------------------------------");
				for (Recommendation recommendation : epnoiCore
						.getInferredRecommendationSpace()
						.getRecommendationsForUserURI(user.getURI())) {
					// System.out.println("Recommendation for "
					// + recommendation.getUserURI());

					System.out.println("         (i)"
							+ recommendation.getItemID());
					System.out.println("         (s)"
							+ recommendation.getStrength());
					System.out.println("         (i.URI)"
							+ recommendation.getItemURI());

				}
			}

			epnoiCore.close();
		}
	}

}
