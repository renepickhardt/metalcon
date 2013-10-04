package de.metalcon.sdd.server.queue;

import de.metalcon.common.Muid;
import de.metalcon.sdd.server.Server;

public class DeleteQueueAction extends QueueAction {

    public DeleteQueueAction(Server server, Muid id) {
        super(server, id);
    }

    @Override
    public void runQueueAction() {
        server.deleteEntityGraph(id);
        server.deleteEntityJson(id);
    }

    @Override
    public int hashCode() {
        int hash = 89320;
        int mult = 503;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }

}
