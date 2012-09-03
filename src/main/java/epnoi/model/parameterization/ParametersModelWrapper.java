package epnoi.model.parameterization;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

public class ParametersModelWrapper {
	public static void write(ParametersModel model, String fileName) {

		try {
			JAXBContext context = JAXBContext.newInstance(ParametersModel.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(this.model, System.out);

			Writer writer = null;
			try {
				writer = new FileWriter(fileName);
				m.marshal(model, writer);
			} finally {
				try {
					writer.close();
				} catch (Exception exception) {
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
	
	public static ParametersModel read(String modelFilePath) {
		ParametersModel model = null;

		try {
			JAXBContext context = JAXBContext.newInstance(ParametersModel.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			Unmarshaller um = context.createUnmarshaller();
			model = (ParametersModel) um
					.unmarshal(new FileReader(modelFilePath));
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
	
		return model;
	}
}
