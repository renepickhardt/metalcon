package de.metalcon.sdd.queue;

import de.metalcon.sdd.Sdd;

public class UpdateDependencyQueueAction<T> extends QueueAction<T> {

    public UpdateDependencyQueueAction(Sdd<T> sdd, T id) {
        super(sdd, id);
    }

    @Override
    public void runQueueAction() {
    }

    @Override
    public int hashCode() {
        int hash = 34155;
        int mult = 541;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
