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

import epnoi.model.Pack;

public class PackWrapper {
	public static Pack extractPack(String packURI) {
		
		Pack pack = new Pack();
	
		int indexOfPacks = packURI.indexOf("=");
		String packID = packURI.substring(indexOfPacks + 1,
				packURI.length());
		pack.setID(new Long(packID));


		pack.setURI(packURI);

		String packResource = "http://www.myexperiment.org/packs/"+packID;
		pack.setResource(packResource);
		
		System.out.println("Extracting pack" + pack.getURI());

		try {
			Document doc = null;
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(pack.getURI() + "&all_elements=yes");
			// doc.getDocumentElement().normalize();

			// Workflow title extraction
			NodeList nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("title");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pack.setTitle(((Element) nodeList.item(0)).getTextContent());
			
			
			// User favourites
			nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("internal-pack-items");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nodeList.getLength() > 0) {
				Node nameNode = nodeList.item(0);

				NodeList workflowsNodeList = ((Element) nameNode)
						.getElementsByTagName("workflow");
				for (int s = 0; s < workflowsNodeList.getLength(); s++) {
					Node fstNode = workflowsNodeList.item(s);
					Element firstWorkflowElement = (Element) fstNode;
					
					String workflowResource = firstWorkflowElement.getAttribute("resource");
					pack.getInternalWorkflows().add(_convertWorkflowResourceToURI(workflowResource));
					
							
				}

				NodeList filesNodeList = ((Element) nameNode)
						.getElementsByTagName("file");
				for (int s = 0; s < filesNodeList.getLength(); s++) {
					Node fstNode = filesNodeList.item(s);
					Element firsFileElement = (Element) fstNode;
					String fileResource = firsFileElement.getAttribute("resource");
					pack.getInternalFiles().add(_convertFileResourceToURI(fileResource));
				}

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

		return pack;
	}
	
	private static String _convertWorkflowResourceToURI(String workflowResource){
		int indexOfWorkflows = workflowResource.indexOf("/workflows/");
		String workflowID = workflowResource.substring(indexOfWorkflows + 11,
				workflowResource.length());
		

		String workflowURI = "http://www.myexperiment.org/workflow.xml?id="
				+ workflowID;
		return workflowURI;
	}
	
	private static String _convertFileResourceToURI(String fileResource){
		int indexOfFiles = fileResource.indexOf("/files/");
		String fileID = fileResource.substring(indexOfFiles + 7,
				fileResource.length());
		

		String fileURIBase = fileResource.substring(0,
				indexOfFiles + 1);
		String fileURI = fileURIBase + "file.xml?id=" + fileID;
		return fileURI;
		
	}
	
	public static void main(String[] args) {
		String packURI = "http://www.myexperiment.org/pack.xml?id=354";
		Pack pack= PackWrapper.extractPack(packURI);
		System.out.println(pack.getTitle());
	}
}
