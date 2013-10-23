package de.metalcon.sdd.queue;

import java.util.Map;

import de.metalcon.sdd.Sdd;

public class UpdateQueueAction<T> extends QueueAction<T> {

    private String type;
    
    private Map<String, String> attrs;
    
    public UpdateQueueAction(Sdd<T> sdd, T id, String type, Map<String, String> attrs) {
        super(sdd, id);
        this.type  = type;
        this.attrs = attrs;
    }
    
    @Override
    public void runQueueAction() {
        sdd.updateEntityImmediately(id, type, attrs);
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        
        // we define that two distinct UpdateQueueActions are never equal
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 53924;
        int mult = 617;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }
    
}
