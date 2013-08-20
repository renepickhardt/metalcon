package de.metalcon.searchServer.Search;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.searchServer.Error.SearchError;

public class SearchServlet extends HttpServlet {

    final private static long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
        throws IOException {
        Map<String, Object> result;
        try {
            SearchRequest sr = new SearchRequest();
            sr.setQuery(request.getParameter("q"));
            result = sr.execute();
        } catch (SearchError e) {
            result = SearchError.toJson(e);
        }
        
        String indent = request.getParameter("indent");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String json = JSONValue.toJSONString(result);
        if (indent != null)
            json = JsonPrettyPrinter.prettyPrintJson(json);
        out.print(json);
        out.flush();
    }
    
}
