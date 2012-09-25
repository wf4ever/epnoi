package epnoi.model.parameterization;

import java.util.Date;

import epnoi.core.EpnoiCore;
import epnoi.recommeders.Recommender;

public class ParametersModelReaderMain {
	public static void main(String[] args) {

		/*
		 * <entry key="server.hostname">localhost</entry> <entry
		 * key="server.path"></entry> <entry key="server.port">8015</entry>
		 */

		System.out
				.println("(START) Testing the parameterization of recommendation algorithms");
		String parametersModelPath = "/parametersModelPath.xml";
		ParametersModel parametersModel = new ParametersModel();

		parametersModel.setIndexPath("indexMyExperiment");
		parametersModel.setModelPath("lastImportedModel.xml");

		parametersModel.setPath("");
		parametersModel.setHostname("localhost");
		parametersModel.setPort("8015");

		CollaborativeFilterRecommenderParameters recommenderA = new CollaborativeFilterRecommenderParameters();
		recommenderA.setURI("recommenderA");
		recommenderA
				.setNeighbourhoodType(Recommender.NEIGHBOURHOOD_TYPE_NEAREST);
		recommenderA.setNeighbourhoohdSize(5);
		recommenderA.setSimilarity(Recommender.SIMILARITY_EUCLIDEAN);
		recommenderA.setType(Recommender.WORKFLOWS_COLLABORATIVE_FILTER);
		recommenderA.setNumberOfRecommendations(5);
		parametersModel.getCollaborativeFilteringRecommender()
				.add(recommenderA);

		CollaborativeFilterRecommenderParameters recommenderB = new CollaborativeFilterRecommenderParameters();
		recommenderB.setURI("recommenderB");
		recommenderB
				.setNeighbourhoodType(Recommender.NEIGHBOURHOOD_TYPE_NEAREST);
		recommenderB.setNeighbourhoohdSize(5);
		recommenderB.setSimilarity(Recommender.SIMILARITY_EUCLIDEAN);
		recommenderB.setType(Recommender.FILES_COLLABORATIVE_FILTER);
		recommenderB.setNumberOfRecommendations(5);

		parametersModel.getCollaborativeFilteringRecommender()
				.add(recommenderB);

		KeywordRecommenderParameters recommenderC = new KeywordRecommenderParameters();
		recommenderC.setURI("recommenderC");
		recommenderC.setIndexPath("/wf4ever/indexMyExperiment");
		recommenderC.setNumberOfQueryHits(10);
		recommenderC.setType(Recommender.KEYWORD_CONTENT_BASED);

		parametersModel.getKeywordBasedRecommender().add(recommenderC);

		ParametersModelReader.write(parametersModel, parametersModelPath);

		ParametersModel readedParametersModel = ParametersModelReader
				.read("/parametersModelPath.xml");
		for (CollaborativeFilterRecommenderParameters recommenderParameters : readedParametersModel
				.getCollaborativeFilteringRecommender()) {
			System.out.println("Recommender> " + recommenderParameters);
			System.out.println(">> " + recommenderParameters);
		}

		System.out.println(" model path "
				+ readedParametersModel.getModelPath());

		System.out
				.println("(END) Testing the parameterization of recommendation algorithms");

	}
}
