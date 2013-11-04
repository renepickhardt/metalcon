package de.metalcon.sdd.queue;

import java.util.Map;

import de.metalcon.sdd.Entity;
import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.error.InconsitentTypeException;
import de.metalcon.sdd.error.InvalidReferenceException;

public class UpdateGraphEntityQueueAction extends QueueAction {
    
    private Entity entity;
    
    private Map<String, String> attrs;
    
    public UpdateGraphEntityQueueAction(Sdd sdd, Entity entity,
                                        Map<String, String> attrs) {
        super(sdd, entity.getId());
        this.entity = entity;
        this.attrs  = attrs;
    }
    
    @Override
    public void runQueueAction()
    throws InvalidReferenceException, InconsitentTypeException {
        sdd.actionUpdateGraphEntity(entity, attrs);
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
        
        hash = hash*mult + ((Long) id).hashCode();
        
        return hash;
    }

}
