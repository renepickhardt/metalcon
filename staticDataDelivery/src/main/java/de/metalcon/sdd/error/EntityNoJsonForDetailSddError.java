package de.metalcon.sdd.error;

import de.metalcon.sdd.Detail;

public class EntityNoJsonForDetailSddError extends SddError {

    private static final long serialVersionUID = 2373821436054610479L;

    private Detail detail;
    
    public EntityNoJsonForDetailSddError(Detail detail) {
        this.detail = detail;
    }
    
    @Override
    public String getDescription() {
        return "An entity didn't generate a JSON repsonse for a detail.";
    }
    
    @Override
    public String getSuggestion() {
        return "The requested detail was \"" + detail +  "\".\n" +
               "Make sure StaticDataDeliveryGenerator generates a JSON response for all detail values.";
    }
    
}
