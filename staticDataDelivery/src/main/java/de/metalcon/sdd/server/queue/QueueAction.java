package de.metalcon.sdd.server.queue;

import de.metalcon.common.Muid;
import de.metalcon.sdd.server.Server;

public abstract class QueueAction implements Comparable<QueueAction> {
    
    protected Server server;
    
    protected Muid id;
    
    public QueueAction(Server server, Muid id) {
        if (server == null || id == null)
            // TODO: handle this
            throw new RuntimeException();
        
        this.server = server;
        this.id     = id;
    }

    public abstract void runQueueAction();
    
    @Override
    public int compareTo(QueueAction other) {
        Class<?> thisclass = getClass();
        Class<?> otherclass = other.getClass();
        
        boolean thisIsImmediate  = thisclass  == UpdateQueueAction.class ||
                                   thisclass  == DeleteQueueAction.class;
        boolean otherIsImmediate = otherclass == UpdateQueueAction.class ||
                                   otherclass == DeleteQueueAction.class;
        
        if (thisIsImmediate) {
            if (otherIsImmediate)
                return 0;
            else
                return -1;
        } else {
            if (otherIsImmediate)
                return 1;
            else
                return 0;
        }
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        QueueAction o = (QueueAction) other;
        return id.equals(o.id);
    }
    
    @Override
    public abstract int hashCode();
    
}
