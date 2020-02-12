package gsearch;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

public class LuceneSingleton {
	private IndexReader indexReader;
	private TaxonomyReader taxoReader;
	private IndexSearcher searcher;
	private Logger logger = Logger.getLogger(LuceneSingleton.class.getName());
	private static LuceneSingleton instance = new LuceneSingleton(); 

	private LuceneSingleton() {
		try {
			logger.info("LuceneSingleton intializing");				
			String gsindexloc = System.getenv("gsindexloc");
			if ( gsindexloc == null ) {
				gsindexloc = "c:/users/karln/opcastorage/azlucene/index";
			}

			String gsindextaxoloc = System.getenv("gsindextaxoloc");
			if ( gsindextaxoloc == null ) {
				gsindextaxoloc = "c:/users/karln/opcastorage/azlucene/indextaxo";
			}
			indexReader = DirectoryReader.open( FSDirectory.open(Paths.get(gsindexloc)));
	        taxoReader = new DirectoryTaxonomyReader(FSDirectory.open(Paths.get(gsindextaxoloc)));
	        searcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			logger.severe("LuceneSingleton fail: " + e.getMessage());				
		}
	}

	public IndexReader getIndexReader() {
		return indexReader;
	}

	public TaxonomyReader getTaxoReader() {
		return taxoReader;
	}

	public IndexSearcher getSearcher() {
		return searcher;
	}

	public static LuceneSingleton getInstance() {
		return instance;
	}
}
