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



// From chapter 7
public class MyExperimentIndexer {
	
	private Model model;
	private String MODEL_PATH = "/model.xml";

  private boolean DEBUG = false;                     //1

  static Set<String> textualMetadataFields           //2
        = new HashSet<String>();                     //2
  static {                                           //2
    textualMetadataFields.add(Metadata.TITLE);       //2
    textualMetadataFields.add(Metadata.AUTHOR);      //2
    textualMetadataFields.add(Metadata.COMMENTS);    //2
    textualMetadataFields.add(Metadata.KEYWORDS);    //2
    textualMetadataFields.add(Metadata.DESCRIPTION); //2
    textualMetadataFields.add(Metadata.SUBJECT);     //2
  }

	private IndexWriter writer;
  
  public static void main(String[] args) throws Exception {

	  System.out.println("Indexing the myExperiment data ");
	   
	  TikaConfig config = TikaConfig.getDefaultConfig();  
    String indexDir = "/indexMyExperiment";
    //String dataDir = "/proofs/lucene/dataTika";
   

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
		writer = new IndexWriter(dir, // 3
				new StandardAnalyzer( // 3
						Version.LUCENE_30),// 3
				true, // 3
				IndexWriter.MaxFieldLength.UNLIMITED); // 3
		
		
		
	try {
		this.model = ModelReader.read(this.MODEL_PATH);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
  
  public void indexURL(String URL) throws Exception{
	
		
		System.out.println("Indexing " + URL);
		Document doc = getDocument(URL);
		writer.addDocument(doc); 
	}
  protected Document getDocument(String URL) throws Exception {

    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, URL);   

    
    InputStream is = new URL(URL+"&all_elements=yes").openStream();

    Parser parser = new AutoDetectParser();       
    ContentHandler handler = new BodyContentHandler(-1);     
    
    ParseContext context = new ParseContext();  
    context.set(Parser.class, parser);          
    try {
      parser.parse(is, handler, metadata,      
                   new ParseContext());        
    } finally {
      is.close();
    }

    Document doc = new Document();

    doc.add(new Field("contents", handler.toString(),          
                      Field.Store.NO, Field.Index.ANALYZED));  
    if (DEBUG) {
      System.out.println("  all text: " + handler.toString());
    }
    
    for(String name : metadata.names()) {         //11
      String value = metadata.get(name);

      if (textualMetadataFields.contains(name)) {
        doc.add(new Field("contents", value,      //12
                          Field.Store.NO, Field.Index.ANALYZED));
      }

      doc.add(new Field(name, value, Field.Store.YES, Field.Index.NO)); //13

      if (DEBUG) {
        System.out.println("  " + name + ": " + value);
      }
    }

    if (DEBUG) {
      System.out.println();
    }

    doc.add(new Field("filename", URL,     //14
             Field.Store.YES, Field.Index.NOT_ANALYZED));

    return doc;
  }
  
  
  public int index() throws Exception{
	  int indexedResources = 0;
	  for(Workflow workflow : model.getWorflows()){
		  //if (workflow.getId()<200){
			  
			  try {
				indexURL(workflow.getURI());
				  indexedResources++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  //}
		
	  }
		return indexedResources;
	}
  
  
  public void close() throws IOException {
		writer.close(); // 4
	}
}



