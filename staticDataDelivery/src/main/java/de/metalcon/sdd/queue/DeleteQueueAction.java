package de.metalcon.sdd.queue;

import de.metalcon.sdd.Sdd;


public class DeleteQueueAction<T> extends QueueAction<T> {

    public DeleteQueueAction(Sdd<T> sdd, T id) {
        super(sdd, id);
    }

    @Override
    public void runQueueAction() {
        sdd.deleteEntityImmediately(id);
    }

    @Override
    public int hashCode() {
        int hash = 89320;
        int mult = 503;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
