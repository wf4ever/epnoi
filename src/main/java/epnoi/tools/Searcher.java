package epnoi.tools;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import epnoi.model.ExternalResourceLucenHelper;


public class Searcher {
	
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new Exception("Usage: java " + Searcher.class.getName()
					+ " <index dir> <query>");
		}
		String indexDir = args[0]; // 1
		String q = args[1]; // 2
		search(indexDir, q);
	}

	public static void search(String indexDir, String q) throws Exception {
		Directory dir = FSDirectory.open(new File(indexDir)); // 3
		IndexSearcher is = new IndexSearcher(dir);
		QueryParser parser = new QueryParser(Version.LUCENE_30, // 4
				"contents", // 4
				new StandardAnalyzer( // 4
						Version.LUCENE_30));
		long start = System.currentTimeMillis();
		Query query = parser.parse(q);
		TopDocs hits = is.search(query, 10); // 5
		long end = System.currentTimeMillis();
		System.err.println("Found " + hits.totalHits
				+ // 6
				" document(s) (in " + (end - start)
				+ " milliseconds) that matched query '" + q + "':");
		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			Document doc = is.doc(scoreDoc.doc); // 7
			System.out.println(doc.get(ExternalResourceLucenHelper.TITLE)+" with the score "+scoreDoc.score); // 8
		}
		is.close(); // 9
	}
}