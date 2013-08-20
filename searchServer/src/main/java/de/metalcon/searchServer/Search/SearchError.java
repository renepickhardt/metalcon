package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.Map;

// This class is not named SearchRuntimeException because that would make all
// other class names to long.
public class SearchError extends RuntimeException {
    
    final private static long serialVersionUID = 1L;
    
    public String what() {
        return "An error in the search component happended.";
    }

    public static Map<String, Object> toJson(SearchError e) {
        Map<String, String> jsonException = new LinkedHashMap<String, String>();
        jsonException.put("class", e.getClass().getName());
        jsonException.put("what",  e.what());
        Map<String, Object> json = new LinkedHashMap<String, Object>();
        json.put("exception", jsonException);
        return json;
    }
    
}
