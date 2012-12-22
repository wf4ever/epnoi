package epnoi.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ORG.oclc.oai.harvester2.verb.ListRecords;

public class OAIPMHRepositoryHarvester {
	public static final String PARAMETER_COMMAND = "-command";
	public static final String PARAMETER_NAME = "-name";
	public static final String PARAMETER_URL = "-URL";
	public static final String PARAMETER_OUT = "-out";
	public static final String PARAMETER_IN = "-in";
	public static final String PARAMETER_FROM = "-from";
	public static final String PARAMETER_TO = "-to";
	public static final String PARAMETER_COMMAND_INIT = "init";
	public static final String PARAMETER_COMMAND_HARVEST = "harvest";

	// ---------------------------------------------------------------------------------------------------------------------------------------

	/*
	 * -command init -out /JUNK2 -URL http://export.arxiv.org/oai2 -name arxive
	 * -command harvest -in /JUNK2/OAIPMH/harvests/arxive -from 2012-04-10 -to 2012-05-10
	 * 
	 * 
	 */
	
	
	public static void main(String[] args) {
		try {

			HashMap<String, String> options = getOptions(args);

			OutputStream out = System.out;

			String command = (String) options.get(PARAMETER_COMMAND);

			if (command != null) {
				if (command.equals(PARAMETER_COMMAND_INIT)) {
					// Add arguments checking here
					_initRepositoryHarvest(options);
				} else if (command.equals(PARAMETER_COMMAND_HARVEST)) {
					_updateRepositoryHarvest(options);
				}
			}

			if (out != System.out)
				out.close();
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------

	private static void _updateRepositoryHarvest(HashMap<String, String> options) {
		String in = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_IN);
		String from = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_FROM);
		String to = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_TO);
		System.out
				.println("Updating the repository harvest with the following paraneters: -in "
						+ in + " -from " + from + " -to " + to);

		Manifest manifest = ManifestHandler.read(in + "/" + "manifest.xml");
		if (manifest != null) {
			System.out.println("Updating the harvest of the repository "
					+ manifest.getRepository() + " in the URL "
					+ manifest.getURL());

			File harvestDirectory = new File(in + "/" + "harvest");
			if (!harvestDirectory.exists()) {
				harvestDirectory.mkdir();
			}

			String metadataPrefix = (String) options.get("-metadataPrefix");
			if (metadataPrefix == null)
				metadataPrefix = "oai_dc";

			String setSpec = (String) options.get("-setSpec");

			// out = new FileOutputStream(outFileName, true);
			Locale locale = Locale.ENGLISH;
			DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
					locale);
			// The initial date is the from parameter date
			System.out.println(">from " + from);
			Date fromDate = null;
			try {
				fromDate = simpleDateFormat.parse(from);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(">fromDate>" + fromDate.toString());

			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);

			Date untilDate = null;
			try {
				untilDate = simpleDateFormat.parse(to);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(">until Date>" + untilDate.toString());
			Date auxDate = null;
			String repositoryDirectoryName = in;
			File repositoryDirectory = new File(repositoryDirectoryName);

			while (fromDate.before(untilDate)) {

				c.add(Calendar.DATE, 1);
				auxDate = c.getTime();

				String outputFileName = repositoryDirectoryName + "/harvest/"
						+ simpleDateFormat.format(fromDate) + ".xml";
				OutputStream outputFile = null;

				if (!new File(outputFileName).isFile()) {
					try {
						outputFile = new FileOutputStream(outputFileName, true);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("harvest(" + manifest.getURL() + ","
							+ simpleDateFormat.format(fromDate) + ","
							+ simpleDateFormat.format(auxDate) + ", "
							+ metadataPrefix + ", " + setSpec + ", "
							+ outputFileName);

					try {
						harvest(manifest.getURL(),
								simpleDateFormat.format(fromDate),
								simpleDateFormat.format(auxDate),
								metadataPrefix, setSpec, outputFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParserConfigurationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SAXException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TransformerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchFieldException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					try {
						outputFile.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				fromDate = auxDate;

			}

		}

	}

	// ---------------------------------------------------------------------------------------------------------------------------------------

	private static void _initRepositoryHarvest(HashMap<String, String> options) {
		String name = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_NAME);
		String URL = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_URL);
		String out = (String) options
				.get(OAIPMHRepositoryHarvester.PARAMETER_OUT);
		System.out
				.println("Initializing repository harvest with the following paraneters: -name "
						+ name + " -URL " + URL + " -out " + out);

		File repositoryDirectory = new File(out + "/OAIPMH/harvests/" + name);
		if (!repositoryDirectory.exists()) {
			boolean success = repositoryDirectory.mkdirs();
			if (success) {
				System.out.println("The directory " + out
						+ " has been successfully created!");

				Manifest manifest = new Manifest();
				manifest.setRepository(name);
				manifest.setURL(URL);
				ManifestHandler
						.marshallToFile(manifest,
								repositoryDirectory.getAbsolutePath()
										+ "/manifest.xml");

				File harvestDirectory = new File(
						repositoryDirectory.getAbsolutePath() + "/harvest");
				harvestDirectory.mkdir();
				/*
				 * try { manifestFile.createNewFile(); } catch (IOException e) {
				 * // TODO Auto-generated catch block e.printStackTrace(); }
				 */
			} else {
				System.out.println(":(");
			}
		} else {
			throw new IllegalArgumentException("The directory "
					+ repositoryDirectory.getAbsolutePath()
					+ " already existed");
		}
	}

	// -----------------------------------------------------------------------------------------------

	public static void harvest2(String baseURL, String from, String until,
			String metadataPrefix, String setSpec, OutputStream out) {
		System.out.println("from> " + from + "until> " + until);
	}

	// -----------------------------------------------------------------------------------------------

	public static void harvest(String baseURL, String from, String until,
			String metadataPrefix, String setSpec, OutputStream out)
			throws IOException, ParserConfigurationException, SAXException,
			TransformerException, NoSuchFieldException {

		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				.getBytes("UTF-8"));
		out.write("<harvest>\n".getBytes("UTF-8"));
		out.write("\n".getBytes("UTF-8"));
		out.write("<timeStamp>".getBytes("UTF-8"));
		Date timeStamp = new Date(System.currentTimeMillis());
		out.write(timeStamp.toString().getBytes("UTF-8"));
		out.write("</timeStamp>".getBytes("UTF-8"));

		out.write("\n".getBytes("UTF-8"));

		ListRecords listRecords = new ListRecords(baseURL, from, until,
				setSpec, metadataPrefix);
		while (listRecords != null) {

			NodeList errors = listRecords.getErrors();
			if (errors != null && errors.getLength() > 0) {
				System.out.println("Found errors");
				int length = errors.getLength();
				for (int i = 0; i < length; ++i) {
					Node item = errors.item(i);
					System.out.println(item);
				}
				System.out.println("Error record: " + listRecords.toString());
				break;
			}

			out.write(listRecords.toString().getBytes("UTF-8"));
			out.write("\n".getBytes("UTF-8"));
			String resumptionToken = listRecords.getResumptionToken();

			if (resumptionToken == null || resumptionToken.length() == 0) {
				listRecords = null;
			} else {

				listRecords = new ListRecords(baseURL, resumptionToken);
			}

		}
		out.write("</harvest>\n".getBytes("UTF-8"));
	}

	// -----------------------------------------------------------------------------------------------

	private static HashMap<String, String> getOptions(String[] args) {
		HashMap<String, String> options = new HashMap<String, String>();
		ArrayList<String> rootArgs = new ArrayList<String>();
		// options.put("rootArgs", rootArgs);

		for (int i = 0; i < args.length; ++i) {
			if (args[i].charAt(0) != '-') {
				rootArgs.add(args[i]);
			} else if (i + 1 < args.length) {
				options.put(args[i], args[++i]);
			} else {
				throw new IllegalArgumentException();
			}
		}
		return options;
	}
}
