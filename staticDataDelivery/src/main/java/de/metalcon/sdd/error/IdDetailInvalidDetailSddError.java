package de.metalcon.sdd.error;

public class IdDetailInvalidDetailSddError extends SddError {

    private static final long serialVersionUID = 8901854902040975901L;
    
    private String detail;
    
    public IdDetailInvalidDetailSddError(String detail) {
        this.detail = detail;
    }

    @Override
    public String getDescription() {
        return "Invalid detail given for IdDetail object construction.";
    }

    @Override
    public String getSuggestion() {
        return "IdDetail request detail was \"" + detail + "\".\n" +
               "It needs to be a valid detail string.";
    }

}
