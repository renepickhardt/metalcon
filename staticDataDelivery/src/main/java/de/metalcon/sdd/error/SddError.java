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
        
        return json;
    }
    
    public void print() {
        System.err.println(getClass().getName());
        System.err.println();
        System.err.println(getDescription());
        System.err.println();
        System.err.println(getSuggestion());
        System.err.println();
        StringWriter trace = new StringWriter();
        printStackTrace(new PrintWriter(trace));
        System.err.println(trace.toString());
    }

}