package de.metalcon.sdd.queue;

import java.util.Map;

import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.error.InvalidDependencyException;

public class UpdateGraphQueueAction<T> extends QueueAction<T> {
    
    private String type;
    
    private Map<String, String> attrs;
    
    public UpdateGraphQueueAction(Sdd<T> sdd, T id, String type,
                                  Map<String, String> attrs) {
        super(sdd, id);
        this.type  = type;
        this.attrs = attrs;
    }
    
    @Override
    public void runQueueAction() throws InvalidDependencyException {
        System.out.println("UpdateGraphQueueAction");
        sdd.actionUpdateGraph(id, type, attrs);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        
        // we define that two distinct UpdateGraphQueueActions are never equal
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 23423;
        int mult = 977;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
