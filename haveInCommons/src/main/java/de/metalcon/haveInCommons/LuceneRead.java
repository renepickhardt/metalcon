/**
 * 
 */
package de.metalcon.haveInCommons;

import java.awt.TextField;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author Jonas Kunze
 * 
 */
public class LuceneRead implements HaveInCommons {
	private IndexWriter writer;
	private IndexSearcher searcher;
	private final String IndexDir = "luceneTest";
	private Analyzer analyzer = null;
	private QueryParser idQueryParser = null;
	private QueryParser neighbourQueryParser = null;

	private HashMap<String, String> storage = new HashMap<String, String>();

	/**
	 * 
	 */
	public LuceneRead() {
	}

	public void createIndexSearcher() {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		try {
			File indexDirFile = new File(this.IndexDir);
			Directory dir = FSDirectory.open(indexDirFile);
			indexReader = DirectoryReader.open(dir);
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.searcher = indexSearcher;
	}

	private void addDoc(String id, String neighbours) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("id", id, Field.Store.YES));
		doc.add(new TextField("neighbours", neighbours, Field.Store.YES));
		this.writer.addDocument(doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(int, int)
	 */
	@Override
	public long[] getCommonNodes(long from, long to) {
		String uuid1 = "" + from;
		String uuid2 = "" + from;
		long[] result = null;
		try {
			Query query0 = neighbourQueryParser.parse(uuid1);
			Query query1 = neighbourQueryParser.parse(uuid2);

			BooleanQuery combiQuery0 = new BooleanQuery();
			combiQuery0.add(query0, BooleanClause.Occur.MUST);
			TopDocs results0 = searcher.search(combiQuery0, 1);

			BooleanQuery combiQuery1 = new BooleanQuery();
			combiQuery1.add(query1, BooleanClause.Occur.MUST);
			TopDocs results1 = searcher.search(combiQuery1, 1);

			BooleanQuery query0AND1 = new BooleanQuery();
			query0AND1.add(combiQuery0, BooleanClause.Occur.MUST);
			query0AND1.add(combiQuery1, BooleanClause.Occur.MUST);

			TopScoreDocCollector collector = TopScoreDocCollector.create(10,
					true);
			searcher.search(query0AND1, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			result = new long[hits.length];
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				result[i] = Integer.parseInt(d.get("id"));
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void putEdge(long from, long to) {

		String value = storage.get(from);
		if (value != null) {
			storage.put("" + from, value + " " + to);
		} else {
			storage.put("" + from, "" + to);
		}

		value = storage.get(to);
		if (value != null) {
			storage.put("" + to, value + " " + from);
		} else {
			storage.put("" + to, "" + from);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void deleteEdge(long from, long to) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Called when all vertices/edges have been pushed (putEdge will not be
	 * called any more)
	 */
	public void putFinished() {
		try {
			Directory dir = FSDirectory.open(new File(this.IndexDir));

			analyzer = new StandardAnalyzer(Version.LUCENE_44);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44,
					analyzer);

			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

			this.writer = new IndexWriter(dir, iwc);

			for (Map.Entry<String, String> entry : storage.entrySet()) {
				addDoc(entry.getKey(), entry.getValue());
			}
			this.writer.close();

			DirectoryReader reader = DirectoryReader.open(dir);
			searcher = new IndexSearcher(reader);
			idQueryParser = new QueryParser(Version.LUCENE_44, "id",
					this.analyzer);
			neighbourQueryParser = new QueryParser(Version.LUCENE_44,
					"neighbours", this.analyzer);

		} catch (IOException e) {
			e.printStackTrace();
		}
		createIndexSearcher();
	}
}
