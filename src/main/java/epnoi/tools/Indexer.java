package epnoi.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// From chapter 1

/**
 * This code was originally written for Erik's Lucene intro java.net article
 */
public abstract class Indexer {
/*
	public static void main(String[] args) throws Exception {
		
		String indexDir = "/proofs/lucene/index";
		String dataDir = "/proofs/lucene/data";

		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();

		System.out.println("Indexing " + numIndexed + " files took "
				+ (end - start) + " milliseconds");
	}
	*/
	private IndexWriter writer;

	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, // 3
				new StandardAnalyzer( // 3
						Version.LUCENE_30),// 3
				true, // 3
				IndexWriter.MaxFieldLength.UNLIMITED); // 3
	}

	public void close() throws IOException {
		writer.close(); // 4
	}
	
	
	/*
	public int indexURLs(String URL) throws Exception{
		indexURL(URL);
	}
	*/
	
	/*
public void indexURL(String URL) throws Exception{
	InputStream inputStream = null;
	inputStream = new URL(URL).openStream();
	
	System.out.println("Indexing " + URL);
	//Document doc = getDocument(URL);
	writer.addDocument(doc); // 10
}
	*/
/*
	public int index(String dataDir, FileFilter filter) throws Exception {

		File[] files = new File(dataDir).listFiles();

		for (File f : files) {
			if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead()
					&& (filter == null || filter.accept(f))) {
				indexFile(f);
			}
		}

		return writer.numDocs(); // 5
	}
*/
	private static class TextFilesFilter implements FileFilter {
		public boolean accept(File path) {
			return path.getName().toLowerCase() // 6
					.endsWith(".txt"); // 6
		}
	}
	
	protected abstract Document getDocument(File f) throws Exception;
/*
	protected Document getDocument(File f) throws Exception {
		Document doc = new Document();
		doc.add(new Field("contents", new FileReader(f))); // 7
		doc.add(new Field("filename", f.getName(), // 8
				Field.Store.YES, Field.Index.NOT_ANALYZED));// 8
		doc.add(new Field("fullpath", f.getCanonicalPath(), // 9
				Field.Store.YES, Field.Index.NOT_ANALYZED));// 9
		return doc;
	}
*/
	private void indexFile(File f) throws Exception {
		System.out.println("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc); // 10
	}
}
