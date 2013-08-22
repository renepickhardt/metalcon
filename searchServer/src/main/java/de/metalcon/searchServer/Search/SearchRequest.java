package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import de.metalcon.searchServer.Error.NonCompleteSearchRequestSearchError;
import de.metalcon.searchServer.Error.SolrServerExceptionSearchError;

public class SearchRequest {

    private String query = null;
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    private void checkIfComplete() {
        if (query == null)
            throw new NonCompleteSearchRequestSearchError(this);
    }
    
    private Map<String, Object> assembleJsonRequestHeader() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("q", query);
        return result;
    }
    
    public Map<String, Object> execute() {
        checkIfComplete();
        
        // -- read from solr
        
        HttpSolrServer solr = new HttpSolrServer(
                "http://localhost:8080/solr/extern");
        
        SolrQuery q = new SolrQuery();
        q.setRequestHandler("/search");
        // maybe vulnerable to xss?
        q.setQuery(query);
        
        QueryResponse qr;
        try {
            qr = solr.query(q);
        } catch (SolrServerException e) {
            throw new SolrServerExceptionSearchError(e);
        }
        
        List<DocExtern> docs = qr.getBeans(DocExtern.class);
        Map<String, Map<String, List<String>>> hl = qr.getHighlighting();
        for (DocExtern doc : docs)
            doc.setHighlight(hl.get(doc.getId()));
        
        // -- assemble json
        
        String jsonSpellcheck = "";
        
        List<Object> jsonInternDocs = new LinkedList<Object>();
        Map<String, Object> jsonIntern = new LinkedHashMap<String, Object>();
        jsonIntern.put("numDocs", 0);
        jsonIntern.put("docs", jsonInternDocs);
        
        List<Object> jsonExternDocs = new LinkedList<Object>();
        for (DocExtern doc : docs)
            jsonExternDocs.add(doc.toJson());
        Map<String, Object> jsonExtern = new LinkedHashMap<String, Object>();
        jsonExtern.put("numDocs", 0);
        jsonExtern.put("docs", jsonExternDocs);
        
        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("requestHeader", assembleJsonRequestHeader());
        json.put("spellcheck",    jsonSpellcheck);
        json.put("intern",        jsonIntern);
        json.put("extern",        jsonExtern);
        
        return json;
    }

}
