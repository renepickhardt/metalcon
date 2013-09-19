package de.metalcon.sdd.error;

public class ReadRequestInvalidIdDetailSddError extends SddError {

    private static final long serialVersionUID = 7489181748317855361L;
    
    private String idDetail;
    
    public ReadRequestInvalidIdDetailSddError(String idDetail) {
        this.idDetail = idDetail;
    }

    @Override
    public String getDescription() {
        return "No Entity exists for requested IdDetail";
    }

    @Override
    public String getSuggestion() {
        return "The IdDetail request was \"" + idDetail + "\".";
    }

}
