package epnoi.model.parameterization;

import epnoi.recommeders.Recommender;

public class ParametersModelReaderMain {
 public static void main(String[] args){
	 System.out.println("(START) Testing the parameterization of recommendation algorithms");
	String parametersModelPath = "/parametersModelPath.xml";
	 ParametersModel parametersModel = new ParametersModel();
	 CollaborativeFilterRecommenderParameters recommenderA= new CollaborativeFilterRecommenderParameters();
	recommenderA.setURI("recommenderA");
	 recommenderA.setNeighbourhoodType(Recommender.NEIGHBOURHOOD_TYPE_NEAREST);
	recommenderA.setNeighbourhoohdSize(5);
	recommenderA.setSimilarity(Recommender.SIMILARITY_EUCLIDEAN);
	recommenderA.setType("deguorflous");
	 parametersModel.getCollaborativeFilteringRecommender().add(recommenderA);
	 
	 ParametersModelWrapper.write(parametersModel, parametersModelPath);
	 
	 
	 ParametersModel readedParametersModel = ParametersModelWrapper.read("/parametersModelPath.xml");
	 for(CollaborativeFilterRecommenderParameters recommenderParameters :readedParametersModel.getCollaborativeFilteringRecommender()){
		 System.out.println("Recommender> "+recommenderParameters);
	 }
	 
	 System.out.println("(END) Testing the parameterization of recommendation algorithms");

 }
}
