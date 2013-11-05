package de.metalcon.sdd.queue;

import java.util.Set;

import de.metalcon.sdd.Entity;
import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.error.InconsitentTypeException;
import de.metalcon.sdd.error.InvalidTypeException;

public class UpdateReferencingQueueAction extends QueueAction {
    
    private Entity entity;
    
    private Set<String> modifiedDetails;
    
    public UpdateReferencingQueueAction(Sdd sdd, Entity entity,
                                        Set<String> modifiedDetails) {
        super(sdd, entity.getId());
        
        this.entity          = entity;
        this.modifiedDetails = modifiedDetails;
    }
    
    @Override
    public void runQueueAction()
    throws InconsitentTypeException, InvalidTypeException {
        sdd.actionUpdateReferencing(entity, modifiedDetails);
    }
    
    @Override
    public QueueActionType getType() {
        return QueueActionType.updateJsonQueueAction;
    }
    
    @Override
    public int hashCode() {
        int hash = 82210;
        int mult = 137;
        
        hash = hash*mult + ((Long) id).hashCode();
        
        return hash;
    }
    
}
