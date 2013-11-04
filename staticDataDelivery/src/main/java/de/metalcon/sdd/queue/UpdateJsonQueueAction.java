package de.metalcon.sdd.queue;

import java.io.IOException;

import de.metalcon.sdd.Entity;
import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.error.InvalidTypeException;

public class UpdateJsonQueueAction extends QueueAction {
    
    private Entity entity;
    
    public UpdateJsonQueueAction(Sdd sdd, Entity entity) {
        super(sdd, entity.getId());
        this.entity = entity;
    }
    
    @Override
    public void runQueueAction()
    throws IOException, InvalidTypeException {
        sdd.actionUpdateJson(entity);
    }
    
    @Override
    public int hashCode() {
        int hash = 91241;
        int mult = 251;
        
        hash = hash*mult + ((Long) id).hashCode();
        
        return hash;
    }

}
