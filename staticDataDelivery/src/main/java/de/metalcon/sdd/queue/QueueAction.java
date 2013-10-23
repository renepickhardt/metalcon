package de.metalcon.sdd.queue;

import de.metalcon.sdd.Sdd;


public abstract class QueueAction<T> implements Comparable<QueueAction<T>> {
    
    protected Sdd<T> sdd;
    
    protected T id;
    
    public QueueAction(Sdd<T> sdd, T id) {
        if (sdd == null)
            throw new IllegalArgumentException("sdd was null");
        if (id == null)
            throw new IllegalArgumentException("id was null");
        
        this.sdd = sdd;
        this.id  = id;
    }

    public abstract void runQueueAction();
    
    @Override
    public int compareTo(QueueAction<T> other) {
        if (other == null)
            return -1;
        
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
        QueueAction<?> o = (QueueAction<?>) other;
        return id.equals(o.id);
    }
    
    @Override
    public abstract int hashCode();
    
}
