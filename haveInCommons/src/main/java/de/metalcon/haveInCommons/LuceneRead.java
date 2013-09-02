/**
 * 
 */
package de.metalcon.haveInCommons;

import java.io.File;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author Rene Pickhardt
 * 
 */
public class LuceneRead implements HaveInCommons {

	/**
	 * 
	 */
	public LuceneRead() {
		Directory dir = FSDirectory.open(new File("luceneTest"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40,
				analyzer);

		if (true) {
			iwc.setOpenMode(OpenMode.CREATE);
		} else {
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		}
		IndexWriter writer = new IndexWriter(dir, iwc);
		
		Document doc = new Document();
		Field pathField = new StringField("neighbour", "patrik", Field.Store.YES);
		doc.add(pathField);
		
		writer.updateDocument(new Term("rene"), doc);

		
		writer.close();

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
		// TODO Auto-generated method stub
		return null;
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
