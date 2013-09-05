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
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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
	protected IndexWriterConfig iwc;
	protected Directory dir;

	private static final String INDEX_DIRECTORY = "luceneBenchmark";

	/**
	 * @throws IOException
	 * 
	 */
	public LuceneRead() throws IOException {
		this.dir = FSDirectory.open(new File(INDEX_DIRECTORY));
		this.analyzer = new StandardAnalyzer(Version.LUCENE_40);
		this.iwc = new IndexWriterConfig(Version.LUCENE_40, this.analyzer);

		if (true) {
			this.iwc.setOpenMode(OpenMode.CREATE);
		} else {
			this.iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		this.iw = new IndexWriter(this.dir, this.iwc);
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

		Query query1 = new TermQuery(new Term("id", uuid1));
		Query query2 = new TermQuery(new Term("id", uuid2));

		BooleanQuery bQuery = new BooleanQuery();
		bQuery.add(query1, BooleanClause.Occur.MUST);

		TopDocs tdocs = null;
		IndexSearcher indexSearcher = new IndexSearcher(this.ir);

		Set<String> items = new HashSet<String>();

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
		try {
			this.iw = new IndexWriter(this.dir, this.iwc);
			this.ir = DirectoryReader.open(this.iw.getDirectory());

			Document doc = new Document();
			Field field = new StringField("neighbour", to, Field.Store.YES);
			doc.add(field);

			// write into "from"-node
			this.iw.updateDocument(new Term("id", from), doc);

			doc = new Document();
			field = new StringField("neighbour", from, Field.Store.YES);
			doc.add(field);

			// write into "to"-node
			this.iw.updateDocument(new Term("id", to), doc);

			this.iw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
