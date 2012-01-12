package epnoi.inferenceengine;

import epnoi.model.Model;

public class ExtendedModelBuilder {
	public static ExtendedModel buildExtededModel(Model model){
		Graph newGraph = GraphBuilder.buildGraph(); 
		ExtendedModel newExtendedModel = new ExtendedModel(model, newGraph);
		return newExtendedModel;
	}

}
