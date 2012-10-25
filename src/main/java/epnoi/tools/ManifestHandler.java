package epnoi.tools;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;


public class ManifestHandler {
	private static final Logger logger = Logger.getLogger(ManifestHandler.class
			.getName());

	/**
	 * 
	 * @param modelFilePath
	 *            The complete path where the XML document is stored
	 * @return It return an initialized instance of the read model. Initialized
	 *         means that it has its internal representation structures ready.
	 */
	public static Manifest read(String modelFilePath) {
		Manifest model = null;

		try {
			JAXBContext context = JAXBContext.newInstance(Manifest.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			Unmarshaller um = context.createUnmarshaller();
			model = (Manifest) um.unmarshal(new FileReader(modelFilePath));
		} catch (PropertyException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {

			logger.severe("The manifest file " + modelFilePath
					+ " could not be found");
		} catch (JAXBException e) {
			logger.severe("The model file " + modelFilePath
					+ " could not be deserialized, some JAXB problem ocurred "
					+ e.getMessage());

		}
		
		return model;
	}
	public static void marshallToFile(Manifest manifest, String fileName) {

		try {
			JAXBContext context = JAXBContext.newInstance(Manifest.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(this.model, System.out);

			Writer w = null;
			try {
				w = new FileWriter(fileName);
				m.marshal(manifest, w);
			} finally {
				try {
					w.close();
				} catch (Exception e) {
				}
			}
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
