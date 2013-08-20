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

import org.json.simple.JSONValue;

public class SearchServlet extends HttpServlet {

    final private static long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        String getQ = request.getParameter("q");
        if (getQ == null) {
            result.put("msg", "No query string.");
        } else {
            Map<String, Object> resultRequest = new LinkedHashMap<String, Object>();
            resultRequest.put("q", getQ);
            
            String spellcheck = "";
            List<Object> internSearchResults = new LinkedList<Object>();
            List<Object> externSearchResults = new LinkedList<Object>();
            
            result.put("request", resultRequest);
            result.put("spellcheck", spellcheck);
            result.put("internSearchResults", internSearchResults);
            result.put("externSearchResults", externSearchResults);
        }
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(JSONValue.toJSONString(result));
        out.flush();
    }
    
}
