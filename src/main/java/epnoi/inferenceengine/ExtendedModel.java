package epnoi.inferenceengine;

import epnoi.model.Model;

public class ExtendedModel {
	private Model model;
	private Graph graph;

	public ExtendedModel(Model model, Graph graph){
		this.model=model;
		this.graph=graph;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
}
