package de.metalcon.sddServer.error;

public class ReadRequestNoQuerySddError extends SddError {

    private static final long serialVersionUID = -1214091231139290350L;

    @Override
    public String getDescription() {
        return "ReadRequest without query.";
    }

    @Override
    public String getSuggestion() {
        return "A ReadRequest was executed without supplying a query string.";
    }

}
