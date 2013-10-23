package de.metalcon.sdd.queue;

import de.metalcon.sdd.Sdd;

public class UpdateDependenciesQueueAction<T> extends QueueAction<T> {
    
    public UpdateDependenciesQueueAction(Sdd<T> sdd, T id) {
        super(sdd, id);
    }
    
    @Override
    public void runQueueAction() {
        System.out.println("UpdateDependenciesQueueAction");
        sdd.actionUpdateDependencies(id);
    }
    
    @Override
    public int hashCode() {
        int hash = 82210;
        int mult = 137;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }
    
}
