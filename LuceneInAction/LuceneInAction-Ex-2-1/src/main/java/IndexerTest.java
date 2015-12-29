import java.io.IOException;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.util	;

import junit.framework.TestCase;

public class IndexerTest extends TestCase {

	protected String[] ids = { "1", "2" };
	protected String[] unindexed = { "Netherlands", "Italy" };
	protected String[] unstored = { "Amsterdam has a lot of bridges", "Venice has a lot of canals" };
	protected static String[] text = { "Amsterdam", "Venice" };
	private Directory directory;

	protected void setUp() throws Exception {
		
		directory = new RAMDirectory();
		IndexWriter writer = getWriter();
		
		for (int i = 0; i < ids.length; i++) {
			Document doc = new Document();
			doc.add(new TextField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("country", unindexed[i], Field.Store.YES));
			doc.add(new TextField("contents", unstored[i], Field.Store.NO));
			doc.add(new TextField("city", text[i], Field.Store.YES));
			writer.addDocument(doc);
		}	
		writer.close();
	}
	
	private IndexWriter getWriter() throws IOException {
		return new IndexWriter(directory, new IndexWriterConfig(new WhitespaceAnalyzer()));
	}
	
	protected int getHitCount(String fieldName, String searchString) throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		Term t = new Term(fieldName, searchString);		
		TermQuery query = new TermQuery(t);
		int hitCount = TestUtil.hitCount(searcher, query);
		reader.close();
		return hitCount;
	}
	
	public void testIndexWriter() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());
		writer.close();
	}

	public void testIndexReader() throws IOException {
		IndexReader reader = DirectoryReader.open(directory);
		assertEquals(ids.length, reader.maxDoc());
		assertEquals(ids.length, reader.numDocs());
		reader.close();
	}
}
