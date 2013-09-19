package de.metalcon.sdd.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public abstract class SddError extends RuntimeException {

    private static final long serialVersionUID = -4717969086691575163L;

    public abstract String getDescription();
    
    public abstract String getSuggestion();
    
    public Map<String, String> toJson() {
        
        Map<String, String> json = new HashMap<String, String>();
        json.put("id",      getClass().getName());
        json.put("desc",    getDescription());
        json.put("suggest", getSuggestion());
        
        StringWriter trace = new StringWriter();
        printStackTrace(new PrintWriter(trace));
        json.put("trace", trace.toString());
        
        System.out.println(getClass().getName());
        System.out.println();
        System.out.println(getDescription());
        System.out.println();
        System.out.println(getSuggestion());
        System.out.println();
        System.out.println(trace.toString());
        System.out.println("-------------------------------------------------");
        return json;
    }

}