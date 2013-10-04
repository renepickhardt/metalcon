package de.metalcon.sdd.server.queue;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Entity;
import de.metalcon.sdd.server.Server;

public class UpdateDependencyQueueAction extends QueueAction {

    public UpdateDependencyQueueAction(Server server, Muid id) {
        super(server, id);
    }

    @Override
    public void runQueueAction() {
        Entity entity = new Entity(server, id);
        server.updateEntityJson(entity);
    }

    @Override
    public int hashCode() {
        int hash = 34155;
        int mult = 541;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
