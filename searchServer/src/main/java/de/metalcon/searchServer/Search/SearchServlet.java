package de.metalcon.searchServer.Search;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class SearchServlet extends HttpServlet {

    final private static long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        response.setContentType("application/json");
        
        JSONObject result = new JSONObject();
        
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }
    
}
