package de.metalcon.sdd.server.queue;

import de.metalcon.common.Muid;
import de.metalcon.sdd.server.Server;

public class DeleteDependencyQueueAction extends QueueAction {

    public DeleteDependencyQueueAction(Server server, Muid id) {
        super(server, id);
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
