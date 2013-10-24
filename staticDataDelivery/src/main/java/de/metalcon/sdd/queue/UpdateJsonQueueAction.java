package de.metalcon.sdd.queue;

import java.io.IOException;

import de.metalcon.sdd.Sdd;

public class UpdateJsonQueueAction<T> extends QueueAction<T> {
    
    public UpdateJsonQueueAction(Sdd<T> sdd, T id) {
        super(sdd, id);
    }
    
    @Override
    public void runQueueAction() throws IOException {
        System.out.println("UpdateJsonQueueAction");
        sdd.actionUpdateJson(id);
    }
    
    @Override
    public int hashCode() {
        int hash = 91241;
        int mult = 251;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
