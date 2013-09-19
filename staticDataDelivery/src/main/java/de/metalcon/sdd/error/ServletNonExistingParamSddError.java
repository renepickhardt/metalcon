package de.metalcon.sdd.error;

public class ServletNonExistingParamSddError extends SddError {

    private static final long serialVersionUID = -1148752266168219992L;
    
    private String key;
    
    public ServletNonExistingParamSddError(String key) {
        this.key = key;
    }
    
    @Override
    public String getDescription() {
        return "Non-optional request parameter did not exist.";
    }
    
    @Override
    public String getSuggestion() {
        return "The requested parameter was \"" + key + "\",";
    }

}
