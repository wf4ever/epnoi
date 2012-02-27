package wrappers.myexperiment;

import epnoi.model.Pack;
import epnoi.model.Workflow;

public class MyExperimentWrappersTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting!");
		Workflow workflow = WorkflowWrapper.extractWorkflow("http://www.myexperiment.org/workflow.xml?id=2746");
		System.out.println("workflow.getTitle()> "+workflow.getTitle());
		System.out.println("workflow.getResource()> "+workflow.getResource());
		
		
		Pack pack = PackWrapper.extractPack("http://www.myexperiment.org/pack.xml?id=169");
		System.out.println("pack.getTitle()> "+pack.getTitle());
		System.out.println("pack.getInternalWorkflows()> "+pack.getInternalWorkflows());

	}

}
