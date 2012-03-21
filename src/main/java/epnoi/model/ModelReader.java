package epnoi.model;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

public class ModelReader {
	/**
	 * 
	 * @param modelFilePath The complete path where the XML document is stored
	 * @return It return an initialized instance of the readed model. Initialized means that it has its internal representation structures ready.
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// After unmarshalling the model, auxiliary data structures must be
		// initialized
		model.init();
		return model;
	}
}
