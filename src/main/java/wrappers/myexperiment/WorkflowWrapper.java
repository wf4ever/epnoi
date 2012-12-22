package wrappers.myexperiment;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import epnoi.model.Workflow;

public class WorkflowWrapper {
	/*
	 * private void _extractWorkflowA(String workflowURI) {
	 * 
	 * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	 * Document doc = null; try { DocumentBuilder db = dbf.newDocumentBuilder();
	 * doc = db.parse(workflowURI); doc.getDocumentElement().normalize(); }
	 * catch (ParserConfigurationException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } catch (SAXException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * NodeList nodeList = null; try { nodeList =
	 * doc.getElementsByTagName("uri"); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * for (int s = 0; s < nodeList.getLength(); s++) { Node fstNode =
	 * nodeList.item(s); Element firstUserElement = (Element) fstNode; Workflow
	 * workflow = this._extractWorkflow(firstUserElement);
	 * model.getWorkflows().add(workflow); }
	 * 
	 * }
	 */

	public static Workflow extractWorkflow(String workflowURI) {
		/*
		 * Long id; String URI; String resource; String description; String
		 * title; String contentType; String contentURI; String uploaderURI;
		 * ArrayList<String> tags;
		 */

		Workflow workflow = new Workflow();
		/*
		 * String workflowResource = workflowElement.getTextContent();
		 * workflow.setResource(workflowResource);
		 */
		int indexOfWorkflows = workflowURI.indexOf("=");
		String workflowID = workflowURI.substring(indexOfWorkflows + 1,
				workflowURI.length());
		workflow.setID(new Long(workflowID));

		String workflowResource = "http://www.myexperiment.org/workflows/"
				+ workflowID;

		workflow.setURI(workflowURI);
		workflow.setResource(workflowResource);

		System.out.println("Extracting workflow" + workflow.getURI());

		try {
			Document doc = null;
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflow.getURI() + "&all_elements=yes");
			// doc.getDocumentElement().normalize();

			// Workflow title extraction
			NodeList nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("title");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (nodeList != null) {
				workflow.setTitle(((Element) nodeList.item(0)).getTextContent());
			}

			nodeList = null;
			try {

				nodeList = doc.getElementsByTagName("content-uri");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (nodeList != null) {

				workflow.setContentURI(((Element) nodeList.item(0))
						.getTextContent());
			}
			
			nodeList = null;
			try {

				nodeList = doc.getElementsByTagName("content-type");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (nodeList != null) {

				workflow.setContentType(((Element) nodeList.item(0))
						.getTextContent());
			}
			
			
			

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workflow;
	}
}
