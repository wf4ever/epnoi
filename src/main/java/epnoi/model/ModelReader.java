package epnoi.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import epnoi.core.EpnoiCore;

public class ModelReader {
	private static final Logger logger = Logger.getLogger(ModelReader.class
			.getName());

	/**
	 * 
	 * @param modelFilePath
	 *            The complete path where the XML document is stored
	 * @return It return an initialized instance of the read model. Initialized
	 *         means that it has its internal representation structures ready.
	 */
	public static Model read(String modelFilePath) {
		Model model = null;

		try {
			JAXBContext context = JAXBContext.newInstance(Model.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			Unmarshaller um = context.createUnmarshaller();
			model = (Model) um.unmarshal(new FileReader(modelFilePath));
		} catch (PropertyException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {

			logger.severe("The model file " + modelFilePath
					+ " could not be found");
		} catch (JAXBException e) {
			logger.severe("The model file " + modelFilePath
					+ " could not be deserialized, some JAXB problem ocurred "
					+ e.getMessage());

		}
		// After unmarshalling the model, auxiliary data structures must be
		// initialized
		model.init();
		return model;
	}
}
