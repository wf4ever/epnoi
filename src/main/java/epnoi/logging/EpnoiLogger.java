package epnoi.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EpnoiLogger {
	static private FileHandler fileTXT;
	static private SimpleFormatter formatterTXT;

	static private FileHandler fileHTML;
	static private Formatter formatterHTML;

	static public void setup(String path) {
		// Create Logger
		System.out.println("path -----> entra "+ path);
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.INFO);
		try {
			fileTXT = new FileHandler(path + "log.txt");
			fileHTML = new FileHandler(path + "log.html");
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		// Create txt Formatter
		formatterTXT = new SimpleFormatter();
		fileTXT.setFormatter(formatterTXT);
		logger.addHandler(fileTXT);

		// Create HTML Formatter
		formatterHTML = new HtmlFormatter();
		fileHTML.setFormatter(formatterHTML);
		logger.addHandler(fileHTML);
	}
}
