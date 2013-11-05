package de.metalcon.sddServer.tomcat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;

public class JsonResponder {
    
    final public static boolean prettyPrint = true;

    public static void writeJsonResponse(HttpServletResponse response,
            Map<String, Object> json) throws IOException {
        writeJsonResponse(response, JSONValue.toJSONString(json));
    }
    
    public static void writeJsonResponse(HttpServletResponse response,
            String json) throws IOException {
        response.setContentType("application/json");
        
        if (prettyPrint)
            json = JsonPrettyPrinter.prettyPrintJson(json);
        
        PrintWriter w = null;
        try {
            w = response.getWriter();
            w.println(json);
            w.flush();
            w.close();
        } finally {
            if (w != null)
                w.close();
        }
    }
}
