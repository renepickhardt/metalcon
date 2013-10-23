package de.metalcon.sdd.queue;

import de.metalcon.sdd.Sdd;

public class DeleteDependencyQueueAction<T> extends QueueAction<T> {

    public DeleteDependencyQueueAction(Sdd<T> sdd, T id) {
        super(sdd, id);
    }

    @Override
    public void runQueueAction() {
    }

    @Override
    public int hashCode() {
        int hash = 12352;
        int mult = 359;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
