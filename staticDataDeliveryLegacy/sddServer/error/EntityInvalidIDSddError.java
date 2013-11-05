package de.metalcon.sddServer.error;

import de.metalcon.common.Muid;

public class EntityInvalidIDSddError extends SddError {

    private static final long serialVersionUID = -4419912308996684424L;
    
    private Muid id;
    
    public EntityInvalidIDSddError(Muid id) {
        this.id = id;
    }
    
    @Override
    public String getDescription() {
        return "Invalid Entity ID.";
    }
    
    @Override
    public String getSuggestion() {
        return "Entity request ID was: \"" + id.toString() + "\".\n" +
               "No entity exists for that ID. Create it first before referencing it.";
    }

}
