package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

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
                "http://localhost:8080/solr/core0");
        
        SolrQuery q = new SolrQuery();
        q.setQuery(query);
        q.setFields("title", "url");
        
        QueryResponse qr;
        try {
            qr = solr.query(q);
        } catch (SolrServerException e) {
            throw new SolrServerExceptionSearchError(e);
        }
        
        SolrDocumentList docs = qr.getResults();
        
        // -- assemble json
        
        String jsonSpellcheck = "";
        
        List<Object> jsonInternSearchResultsDocs = new LinkedList<Object>();
        Map<String, Object> jsonInternSearchResults =
                new LinkedHashMap<String, Object>();
        jsonInternSearchResults.put("numDocs", 0);
        jsonInternSearchResults.put("docs", jsonInternSearchResultsDocs);
        
        List<Object> jsonExternSearchResultsDocs = new LinkedList<Object>();
        for (SolrDocument doc : docs)
            jsonExternSearchResultsDocs.add(doc);
        Map<String, Object> jsonExternSearchResults =
                new LinkedHashMap<String, Object>();
        jsonExternSearchResults.put("numDocs", 0);
        jsonExternSearchResults.put("docs", jsonExternSearchResultsDocs);
        
        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("requestHeader",       assembleJsonRequestHeader());
        json.put("spellcheck",          jsonSpellcheck);
        json.put("internSearchResults", jsonInternSearchResults);
        json.put("externSearchResults", jsonExternSearchResults);
        
        return json;
    }

}
