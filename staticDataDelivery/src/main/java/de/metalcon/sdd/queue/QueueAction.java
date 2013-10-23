package de.metalcon.sdd.queue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.metalcon.sdd.Sdd;


public abstract class QueueAction<T> implements Comparable<QueueAction<T>> {
    
    private static final Map<Class<?>, Integer> classPriority;
    static {
        Map<Class<?>, Integer> p = new HashMap<Class<?>, Integer>();
        p.put(UpdateGraphQueueAction.class,        1);
        p.put(UpdateJsonQueueAction.class,         2);
        p.put(UpdateDependenciesQueueAction.class, 3);
        classPriority = Collections.unmodifiableMap(p);
    }
    
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

    public abstract void runQueueAction() throws Exception;
    
    @Override
    public int compareTo(QueueAction<T> other) {
        if (other == null)
            return -1;
        
        int thisPriority  = classPriority.get(getClass());
        int otherPriority = classPriority.get(other.getClass());
        
        if (thisPriority == otherPriority)
            return 0;
        else if (thisPriority > otherPriority)
            return 1;
        else
            return -1;
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
