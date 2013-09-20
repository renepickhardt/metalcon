package de.metalcon.sdd.error;

public class RequestMissingParamIdSddError extends SddError {

    private static final long serialVersionUID = 2805187622643955680L;

    @Override
    public String getDescription() {
        return "Request without id parameter";
    }

    @Override
    public String getSuggestion() {
        return "The request was missing the required \"id\" parameter.";
    }

}
