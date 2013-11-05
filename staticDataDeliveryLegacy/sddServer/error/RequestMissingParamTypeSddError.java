package de.metalcon.sddServer.error;

public class RequestMissingParamTypeSddError extends SddError {

    private static final long serialVersionUID = 2177524424694187493L;

    public String getDescription() {
        return "Request without type parameter.";
    }

    public String getSuggestion() {
        return "The request was missing the required \"type\" parameter.";
    }

}
