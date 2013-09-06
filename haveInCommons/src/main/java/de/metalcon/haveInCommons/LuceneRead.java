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
	protected IndexWriterConfig iwc;
	protected Directory dir;

	private static final String INDEX_DIRECTORY = "luceneBenchmark";

	/**
	 * @throws IOException
	 * 
	 */
	public LuceneRead() throws IOException {
		
		//TODO: proper initialization
		this.dir = FSDirectory.open(new File(INDEX_DIRECTORY));
		this.analyzer = new StandardAnalyzer(Version.LUCENE_44);
		this.iwc = new IndexWriterConfig(Version.LUCENE_44, this.analyzer);

		if (true) {
			this.iwc.setOpenMode(OpenMode.CREATE);
		} else {
			//TODO: fix me!
			this.iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		this.iw = new IndexWriter(this.dir, this.iwc);
		try {
			this.ir = DirectoryReader.open(this.iw.getDirectory());
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		IndexSearcher indexSearcher = new IndexSearcher(this.ir);
		
		// setup query for uuid1
		BooleanQuery combiQuery1 = new BooleanQuery();
	    combiQuery1.add(query1, BooleanClause.Occur.MUST);

	    // setup query for uuid2
	    BooleanQuery combiQuery2 = new BooleanQuery();
	    combiQuery2.add(query2, BooleanClause.Occur.MUST);

	    // combine to form intersection of entities
	    BooleanQuery query1AND2 = new BooleanQuery();
	    query1AND2.add(combiQuery1, BooleanClause.Occur.MUST);
	    query1AND2.add(combiQuery2, BooleanClause.Occur.MUST);

	    TopDocs results1AND2 = null;

	    try {
			results1AND2 = indexSearcher.search(query1AND2, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ScoreDoc[] scoreDocs = results1AND2.scoreDocs;
		String[] val = null;
	    for(ScoreDoc scoreDoc: scoreDocs){
	    	try {
				Document doc = indexSearcher.doc(scoreDoc.doc);
				val = doc.getValues("neighbour");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    Set<String> items = new HashSet<String>();
	    
	    for(String s : val){
	    	items.add(s);
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
		// TODO Search for Term and delete all ocurrences as neighbour
		return false;
	}

}
