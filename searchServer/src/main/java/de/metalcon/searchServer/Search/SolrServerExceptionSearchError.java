package de.metalcon.searchServer.Search;

import org.apache.solr.client.solrj.SolrServerException;

public class SolrServerExceptionSearchError extends SearchError {

    final private static long serialVersionUID = 1L;
    
    private String solrMsg;
    
    public SolrServerExceptionSearchError(SolrServerException e) {
        super();
        solrMsg = e.toString();
    }
    
    public String what() {
        return "SolrServerError: " + solrMsg;
    }
    
}
