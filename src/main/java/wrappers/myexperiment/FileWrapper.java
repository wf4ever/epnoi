package wrappers.myexperiment;

import org.w3c.dom.Element;

import epnoi.model.File;

public class FileWrapper {
	
	public static File extractFile(String fileURI){
		
			

			File file = new File();
			file.setURI(fileURI);
		
			int indexOfWorkflows = fileURI.indexOf("=");
			
			String workflowID = fileURI.substring(indexOfWorkflows + 1,
					fileURI.length());
			file.setID(new Long(workflowID));

			String fileResource = "http://www.myexperiment.org/files/"+workflowID;
			file.setResource(fileResource);
			
			return file;
		
	}

}
