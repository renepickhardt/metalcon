package de.metalcon.sdd.queue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.metalcon.sdd.Sdd;


public abstract class QueueAction implements Comparable<QueueAction> {
    
    private static final Map<Class<?>, Integer> classPriority;
    static {
        Map<Class<?>, Integer> p = new HashMap<Class<?>, Integer>();
        p.put(UpdateGraphEntityQueueAction.class, 1);
        p.put(UpdateGraphRelQueueAction.class,    2);
        p.put(UpdateGraphRelsQueueAction.class,   2);
        p.put(UpdateJsonQueueAction.class,        3);
        p.put(UpdateReferencingQueueAction.class, 4);
        classPriority = Collections.unmodifiableMap(p);
    }
    
    // TODO: is int a good choice for this?
    private static int insertCounter = 0;
    
    private int insertCount;
    
    protected Sdd sdd;
    
    protected long id;
    
    public QueueAction(Sdd sdd, long id) {
        if (sdd == null)
            throw new IllegalArgumentException("sdd was null");
        
        insertCount = ++insertCounter;
        
        this.sdd = sdd;
        this.id  = id;
    }

    public abstract void runQueueAction() throws Exception;
    
    public abstract QueueActionType getType();
    
    @Override
    public int compareTo(QueueAction other) {
        if (other == null)
            return -1;
        
        int thisPriority  = classPriority.get(getClass());
        int otherPriority = classPriority.get(other.getClass());
        
        if (thisPriority == otherPriority)
            // (a - b) < 0 rather than a < b because of possible overflow
            return (insertCount - other.insertCount) < 0 ? -1 : 1;
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
        QueueAction o = (QueueAction) other;
        return id == o.id;
    }
    
    @Override
    public abstract int hashCode();
    
}
