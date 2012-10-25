package epnoi.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import epnoi.model.Model;
import epnoi.model.ModelReader;
import epnoi.model.Workflow;

public class MyExperimentIndexer {

	private Model model;
	private String MODEL_PATH = "/lastImportedModel.xml";

	private boolean DEBUG = false; 

	static Set<String> textualMetadataFields 
	= new HashSet<String>();
	static { 
		textualMetadataFields.add(Metadata.TITLE); 
		textualMetadataFields.add(Metadata.AUTHOR);
		textualMetadataFields.add(Metadata.COMMENTS);
		textualMetadataFields.add(Metadata.KEYWORDS);
		textualMetadataFields.add(Metadata.DESCRIPTION); 
		textualMetadataFields.add(Metadata.SUBJECT); 
	}

	private IndexWriter writer;

	public static void main(String[] args) throws Exception {

		System.out.println("Indexing the myExperiment data ");

		TikaConfig config = TikaConfig.getDefaultConfig();
		String indexDir = "/JUNK/indexMyExperiment";
		// String dataDir = "/proofs/lucene/dataTika";

		long start = new Date().getTime();
		MyExperimentIndexer indexer = new MyExperimentIndexer(indexDir);
		int numIndexed = indexer.index();
		indexer.close();
		long end = new Date().getTime();

		System.out.println("Indexing " + numIndexed + " files took "
				+ (end - start) + " milliseconds");
	}

	public MyExperimentIndexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, 
				new StandardAnalyzer( 
						Version.LUCENE_30),
				true, 
				IndexWriter.MaxFieldLength.UNLIMITED); 

		try {
			this.model = ModelReader.read(this.MODEL_PATH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void indexURL(String URL) throws Exception {

		System.out.println("Indexing " + URL);
		Document doc = getDocument(URL);
		writer.addDocument(doc);
	}

	protected Document getDocument(String URL) throws Exception {

		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, URL);

		InputStream is = new URL(URL + "&all_elements=yes").openStream();

		Parser parser = new AutoDetectParser();
		ContentHandler handler = new BodyContentHandler(-1);

		ParseContext context = new ParseContext();
		context.set(Parser.class, parser);
		try {
			parser.parse(is, handler, metadata, new ParseContext());
		} finally {
			is.close();
		}

		Document newDocument = new Document();

		newDocument.add(new Field("contents", handler.toString(), Field.Store.NO,
				Field.Index.ANALYZED));
		if (DEBUG) {
			System.out.println("  all text: " + handler.toString());
		}

		for (String name : metadata.names()) { 
			String value = metadata.get(name);

			if (textualMetadataFields.contains(name)) {
				newDocument.add(new Field("contents", value, 
						Field.Store.NO, Field.Index.ANALYZED));
			}

			newDocument.add(new Field(name, value, Field.Store.YES, Field.Index.NO)); 

			if (DEBUG) {
				System.out.println("  " + name + ": " + value);
			}
		}

		if (DEBUG) {
			System.out.println();
		}

		newDocument.add(new Field("uri", URL,
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		return newDocument;
	}

	public int index() throws Exception {
		int indexedResources = 0;
		for (Workflow workflow : model.getWorkflows()) {
			
			try {
				indexURL(workflow.getURI());
				indexedResources++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		}
		return indexedResources;
	}

	public void close() throws IOException {
		writer.close(); // 4
	}
}
