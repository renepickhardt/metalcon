package de.metalcon.haveInCommons;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author Rene Pickhardt
 * 
 */
public class LuceneRead implements HaveInCommons {

	protected IndexWriter iw;
	protected StandardAnalyzer analyzer;
	protected IndexReader ir;
	private static final String INDEX_DIRECTORY = "luceneBenchmark";

	/**
	 * @throws IOException
	 * 
	 */
	public LuceneRead() throws IOException {
		Directory dir = FSDirectory.open(new File(INDEX_DIRECTORY));
		this.analyzer = new StandardAnalyzer(Version.LUCENE_40);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40,
				this.analyzer);

		if (true) {
			iwc.setOpenMode(OpenMode.CREATE);
		} else {
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		this.iw = new IndexWriter(dir, iwc);
		try {
			this.ir = DirectoryReader.open(this.iw.getDirectory());
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * Document doc = new Document(); Field pathField = new
		 * StringField("neighbour", "patrik", Field.Store.YES);
		 * doc.add(pathField);
		 * 
		 * this.iw.updateDocument(new Term("rene"), doc);
		 * 
		 * this.iw.close();
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.metalcon.haveInCommons.HaveInCommons#getCommonNodes(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Set<String> getCommonNodes(String uuid1, String uuid2) {
		Term term = new Term("nodeID", uuid1);
		Query query = new TermQuery(term);
		TopDocs tdocs = null;
		IndexSearcher indexSearcher = new IndexSearcher(this.ir);

		try {
			tdocs = indexSearcher.search(query, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<String> items = new HashSet<String>();
		// TODO: Make assumptions about run-time complexity
		if (tdocs.totalHits < 0) {
			ScoreDoc[] scoreDocsArray = tdocs.scoreDocs;
			for (ScoreDoc scoredoc : scoreDocsArray) {
				Document doc = null;
				try {
					doc = indexSearcher.doc(scoredoc.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IndexableField[] fields = doc.getFields("nodeId");
				for (IndexableField field : fields) {
					items.add(field.stringValue());
				}
			}
		}

		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#putEdge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void putEdge(String from, String to) {
		// TODO Auto-generated method stub

		// first, search if document with nodeId already exists

		// search for node "from"
		IndexReader reader = null;
		TopDocs tdocs = null;
		try {
			reader = DirectoryReader.open(this.iw.getDirectory());
		} catch (IOException e) {
			e.printStackTrace();
		}

		IndexSearcher indexSearcher = new IndexSearcher(reader);
		Term term = new Term("nodeId", from);
		Query query = new TermQuery(term);

		try {
			// TODO: Can we assume that every node is unique? Otherwise we have
			// to obtain more results
			tdocs = indexSearcher.search(query, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tdocs = null;
		}

		// obtain the document and insert new "to" node, otherwise add a new
		// document
		if (tdocs != null && tdocs.totalHits != 0) {
			ScoreDoc[] scoreDocsArray = tdocs.scoreDocs;
			for (ScoreDoc scoredoc : scoreDocsArray) {
				Document doc = null;
				try {
					doc = indexSearcher.doc(scoredoc.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Field pathfield = new StringField("nodeId", to, Field.Store.YES);
				doc.add(pathfield);
			}
		} else {
			Document doc = new Document();
			Field pathfield = new StringField("nodeId", to, Field.Store.YES);
			doc.add(pathfield);
			try {
				this.iw.addDocument(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// search for node "to"
		reader = null;
		tdocs = null;

		term = new Term("nodeId", to);
		query = new TermQuery(term);

		try {
			// TODO: Can we assume that every node is unique? Otherwise we have
			// to obtain more results
			tdocs = indexSearcher.search(query, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tdocs = null;
		}

		// obtain the document and insert new "to" node, otherwise add a new
		// document
		if (tdocs != null && tdocs.totalHits != 0) {
			ScoreDoc[] scoreDocsArray = tdocs.scoreDocs;
			for (ScoreDoc scoredoc : scoreDocsArray) {
				Document doc = null;
				try {
					doc = indexSearcher.doc(scoredoc.doc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Field pathfield = new StringField("nodeId", from,
						Field.Store.YES);
				doc.add(pathfield);
			}
		} else {
			Document doc = new Document();
			Field pathfield = new StringField("nodeId", from, Field.Store.YES);
			doc.add(pathfield);
			try {
				this.iw.addDocument(doc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.metalcon.haveInCommons.HaveInCommons#delegeEdge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean delegeEdge(String from, String to) {
		// TODO Auto-generated method stub
		return false;
	}

}
