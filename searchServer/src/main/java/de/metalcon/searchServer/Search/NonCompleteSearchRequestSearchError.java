package de.metalcon.searchServer.Search;

public class NonCompleteSearchRequestSearchError extends SearchError {

    final private static long serialVersionUID = 1L;
    
    public NonCompleteSearchRequestSearchError(SearchRequest sr) {
        super();
    }
    
    public String what() {
        return "A non complete SearchRequest was executed.";
    }
    
}
