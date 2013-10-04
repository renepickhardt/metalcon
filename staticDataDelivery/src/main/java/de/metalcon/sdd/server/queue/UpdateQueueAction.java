package de.metalcon.sdd.server.queue;

import java.util.Map;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Entity;
import de.metalcon.sdd.server.Server;

public class UpdateQueueAction extends QueueAction {

    private Map<String, String[]> params;
    
    public UpdateQueueAction(Server server, Muid id,
                             Map<String, String[]> params) {
        super(server, id);
        this.params = params;
    }
    
    @Override
    public void runQueueAction() {
        Entity entity = new Entity(server, params);
        server.updateEntityGraph(entity);
        server.updateEntityJson(entity);
        server.updateEntityDependencies(entity.getId());
        
//        UpdateDependencyQueueAction action =
//                new UpdateDependencyQueueAction(server, id);
//        if (!server.queueAction(action))
//            // TODO: handle this
//            throw new RuntimeException();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        
        // we define that two distinct UpdateQueueActions are never equal
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 53924;
        int mult = 617;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }
    
}
