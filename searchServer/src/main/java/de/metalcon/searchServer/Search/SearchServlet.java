package de.metalcon.searchServer.Search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONValue;

public class SearchServlet extends HttpServlet {

    final private static long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse)
        throws IOException {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        String getQ = httpRequest.getParameter("q");
        if (getQ == null) {
            result.put("msg", "No query string.");
        } else {
            HttpSolrServer solr = new HttpSolrServer(
                    "http://localhost:8080/solr/core0");
            
            SolrQuery query = new SolrQuery();
            query.setQuery(getQ);
            
            QueryResponse response = null;
            try {
                response = solr.query(query);
            } catch (SolrServerException e) {
                result.put("msg", "SolrServerException: " + e);
            }
            SolrDocumentList results = response.getResults();
            
            
            Map<String, Object> request = new LinkedHashMap<String, Object>();
            request.put("q", getQ);
            
            String spellcheck = "";
            List<Object> internSearchResults = new LinkedList<Object>();
            List<Object> externSearchResults = new LinkedList<Object>();
            for (int i = 0; i != results.size(); ++i) {
                externSearchResults.add(results.get(i));
            }
            
            result.put("request", request);
            result.put("spellcheck", spellcheck);
            result.put("internSearchResults", internSearchResults);
            result.put("externSearchResults", externSearchResults);
        }
        
        httpResponse.setContentType("application/json");
        PrintWriter out = httpResponse.getWriter();
        out.print(JSONValue.toJSONString(result));
        out.flush();
    }
    
}
