package de.metalcon.sdd.error;

public class EntityJsonClassCastSddError extends SddError {

    private static final long serialVersionUID = -1118137938784784440L;
    
    private String json;
    
    public EntityJsonClassCastSddError(String json) {
        this.json = json;
    }

    @Override
    public String getDescription() {
        return "A exception occured while casting the result of a JSON Parser.";
    }

    @Override
    public String getSuggestion() {
        return "The JSON string needs to be in valid JSON format.\n\n" +
               "This is the JSON string after whose parsing the class cast exception occured:\n" +
               json;
    }

}
