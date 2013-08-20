package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

public class SearchRequest {

    private String query = null;
    
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    public void checkIfComplete() {
        if (query == null)
            throw new NonCompleteSearchRequestSearchError(this);
    }
    
    public Map<String, Object> execute() {
        checkIfComplete();
        
        // -- read from solr
        
        HttpSolrServer solr = new HttpSolrServer(
                "http://localhost:8080/solr/core0");
        
        SolrQuery q = new SolrQuery();
        q.setQuery(query);
        
        QueryResponse r;
        try {
            r = solr.query(q);
        } catch (SolrServerException e) {
            throw new SolrServerExceptionSearchError(e);
        }
        
        SolrDocumentList docs = r.getResults();
        
        // -- assemble json
        
        Map<String, Object> jsonRequest = new LinkedHashMap<String, Object>();
        jsonRequest.put("q", query);
        
        String jsonSpellcheck = "";
        
        List<Object> jsonInternSearchResults = new LinkedList<Object>();
        
        List<Object> jsonExternSearchResults = new LinkedList<Object>();
        for (int i = 0; i != docs.size(); ++i)
            jsonExternSearchResults.add(docs.get(i));
        
        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("request",             jsonRequest);
        json.put("spellcheck",          jsonSpellcheck);
        json.put("internSearchResults", jsonInternSearchResults);
        json.put("externSearchResults", jsonExternSearchResults);
        
        return json;
    }

}
