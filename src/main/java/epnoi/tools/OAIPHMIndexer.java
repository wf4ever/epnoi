package epnoi.tools;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import epnoi.model.ExternalResourceLucenHelper;

// --------------------------------------------------------------------------------------------------------------------------

public class OAIPHMIndexer {
	public static final String PARAMETER_COMMAND = "-command";
	public static final String PARAMETER_NAME = "-name";
	public static final String PARAMETER_URL = "-URL";
	public static final String PARAMETER_OUT = "-out";
	public static final String PARAMETER_IN = "-in";
	public static final String PARAMETER_FROM = "-from";
	public static final String PARAMETER_TO = "-to";
	public static final String PARAMETER_COMMAND_INIT = "init";
	public static final String PARAMETER_COMMAND_HARVEST = "harvest";

	private String MODEL_PATH = "/lastImportedModel.xml";

	private boolean DEBUG = true;

	private IndexWriter writer;

	/*
	 * OAIPMHIndexer -in where-oaipmh-harvest-dir -repository name
	 */
	public static void main(String[] args) throws Exception {

		HashMap<String, String> options = getOptions(args);

		OutputStream out = System.out;

		
		String in = (String) options.get(PARAMETER_IN);

		String indexDir = in + "/OAIPMH/index";
		String name = (String) options.get(PARAMETER_NAME);

		String harvestDir = in + "/OAIPMH/harvests/" + name + "/harvest";

		long start = new Date().getTime();
		OAIPHMIndexer indexer = new OAIPHMIndexer(indexDir);
		int numIndexed = 0;

		File folder = new File(harvestDir);
		File[] listOfFiles = folder.listFiles();
		System.out.println("Indexing the directory/repository "
				+ folder.getAbsolutePath());
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String filePath = "file://" + listOfFiles[i].getAbsolutePath();
				System.out.println("Found the file: " + filePath);
				if (filePath.endsWith(".xml")) {
					indexer.indexHarvestFile(filePath);

				}

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

		indexer.close();
		long end = new Date().getTime();

		System.out.println("Indexing " + numIndexed + " files took "
				+ (end - start) + " milliseconds");
	}

	public OAIPHMIndexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30),
				true, IndexWriter.MaxFieldLength.UNLIMITED);

	}

	// --------------------------------------------------------------------------------------------------------------------------

	public void indexHarvestFile(String filepath) throws Exception {

		System.out
				.println("Indexing the file -------------------------------------------"
						+ filepath);

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(false);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		org.w3c.dom.Document doc = builder.parse(filepath);
		XPath xpath = XPathFactory.newInstance().newXPath();
		// XPath Query for showing all nodes value
		XPathExpression expr = xpath.compile("//record");

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		for (int i = 0; i < nodes.getLength(); i++) {

			Element recordElement = (Element) nodes.item(i);

			Document document = _indexRecord(recordElement);

			String documentURI = document.get(ExternalResourceLucenHelper.URI);
			writer.updateDocument(new Term(ExternalResourceLucenHelper.URI,
					documentURI), document);
			System.out.println("Writting "
					+ (document.getValues(ExternalResourceLucenHelper.URI)[0]));
		}

		// Document doc = getDocument(URL);
		// writer.addDocument(doc);
	}

	private Document _indexRecord(Element recordElement) {

		Document newDocument = new Document();

		// newDocument.add(new Field("contents", handler.toString(),
		// Field.Store.NO, Field.Index.ANALYZED));

		NodeList newnodes = recordElement.getElementsByTagName("dc:title");

		for (int j = 0; j < newnodes.getLength(); j++) {
			/*
			 * System.out.println("Title!!!----> " +
			 * newnodes.item(j).getTextContent() +
			 * " ---------------------------------");
			 */
			newDocument.add(new Field(ExternalResourceLucenHelper.TITLE,
					newnodes.item(j).getTextContent(), Field.Store.YES,
					Field.Index.ANALYZED));

		}

		newnodes = recordElement.getElementsByTagName("dc:identifier");

		for (int j = 0; j < newnodes.getLength(); j++) {
			String identifier = newnodes.item(j).getTextContent();
			// System.out.println("Identifier----> " + identifier);
			if (identifier.startsWith("http://")) {
				// System.out.println("Este es el URL " + identifier);
				newDocument.add(new Field(ExternalResourceLucenHelper.URI,
						newnodes.item(j).getTextContent(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
			}
		}

		newnodes = recordElement.getElementsByTagName("dc:creator");

		for (int j = 0; j < newnodes.getLength(); j++) {
			// System.out.println("Creator----> "
			// + newnodes.item(j).getTextContent());

		}

		newnodes = recordElement.getElementsByTagName("dc:description");
		for (int j = 0; j < newnodes.getLength(); j++) {
			// System.out.println("Description----> "
			// + newnodes.item(j).getTextContent());
			newDocument.add(new Field(ExternalResourceLucenHelper.CONTENT,
					newnodes.item(j).getTextContent(), Field.Store.NO,
					Field.Index.ANALYZED));

		}

		return newDocument;

	}

	// --------------------------------------------------------------------------------------------------------------------------

	public void close() throws IOException {
		writer.close(); // 4
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
