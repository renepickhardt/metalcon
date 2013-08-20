package de.metalcon.searchServer.Search;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SearchServlet extends HttpServlet {

    final private static long serialVersionUID = 1L;
    
    // To avoid warnings with JSONObject.put(). There is no other way to avoid
    // warnings with JSON-simple library. See:
    // http://stackoverflow.com/questions/16415436/json-and-generics-in-java-type-safety-warning
    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        response.setContentType("application/json");
        
        String    spellcheck          = "";
        JSONArray internSearchResults = new JSONArray();
        JSONArray externSearchResults = new JSONArray();
        JSONObject result = new JSONObject();
        result.put("spellcheck", spellcheck);
        result.put("internSearchResults", internSearchResults);
        result.put("externSearchResults", externSearchResults);
        
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }
    
}
