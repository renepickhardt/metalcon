package de.metalcon.sdd.error;

public class IdDetailInvalidStringSddError extends SddError {

    private static final long serialVersionUID = -8778019434937798676L;
    
    private String idDetail;
    
    public IdDetailInvalidStringSddError(String idDetail) {
        this.idDetail = idDetail;
    }

    @Override
    public String getDescription() {
        return "Invalid IdDetail string given for IdDetail object construction.";
    }

    @Override
    public String getSuggestion() {
        return "The IdDetail request string was \"" + idDetail + "\".\n" +
               "It should have the syntax of \"<id>:<detail>\".";
    }

}
