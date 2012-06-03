package epnoi.inferenceengine;

import epnoi.model.Model;

public class ExtendedModelBuilder {
	public static ExtendedModel buildExtendedModel(Model model){
		Graph newGraph=null;
		if (model!=null){
		newGraph = GraphBuilder.buildGraph(model); 
		
	}
	else{
		newGraph = GraphBuilder.buildGraph(); 
	}
	ExtendedModel newExtendedModel = new ExtendedModel(model, newGraph);
	return newExtendedModel;
	}

}
