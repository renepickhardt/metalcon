package de.metalcon.sdd.error;

public class EntityInvalidEntityTypeSddError extends SddError {

    private static final long serialVersionUID = 2245633530376593538L;
    
    private String type;
    
    public EntityInvalidEntityTypeSddError(String type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return "Invalid Entity type given while trying to determine Entity class.";
    }

    @Override
    public String getSuggestion() {
        return "Entity type requested was \"" + type + "\"." +
               "Valid entity types are created by StaticDataDeliveryGenerator.";
    }

}
