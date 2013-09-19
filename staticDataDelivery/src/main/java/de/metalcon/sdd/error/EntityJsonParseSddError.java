package de.metalcon.sdd.error;

import org.json.simple.parser.ParseException;

public class EntityJsonParseSddError extends SddError {

    private static final long serialVersionUID = -4003997605537223015L;
    
    private ParseException parseException;
    
    private String json;
    
    public EntityJsonParseSddError(ParseException parseException,
                                    String json) {
        this.parseException = parseException;
        this.json = json;
    }
    
    @Override
    public String getDescription() {
        return "An exception occured while parsing a JSON string of an Entity.";
    }
    
    public String getSuggestion() {
        return "The JSON string needs to be in valid JSON format.\n\n" +
               "This is the error message of the parser:\n" +
               parseException.toString() + "\n\n" +
               "This is the JSON string the parse error occured on:\n" +
               json;
    }

}
