package de.metalcon.sdd.queue;

import de.metalcon.sdd.Entity;
import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.error.InconsitentTypeException;
import de.metalcon.sdd.error.InvalidReferenceException;
import de.metalcon.sdd.error.InvalidTypeException;

public class UpdateGraphRelsQueueAction extends QueueAction {
    
    private Entity entity;
    
    private String attr;
    
    private long[] rels;
    
    public UpdateGraphRelsQueueAction(Sdd sdd, Entity entity,
                                     String attr, long[] rels) {
        super(sdd, entity.getId());
        this.entity = entity;
        this.attr   = attr;
        this.rels   = rels;
    }
    
    @Override
    public void runQueueAction()
    throws InvalidReferenceException, InconsitentTypeException,
           InvalidTypeException {
        sdd.actionUpdateGraphRels(entity, attr, rels);
    }
    
    @Override
    public QueueActionType getType() {
        return QueueActionType.updateGraphQueueAction;
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
