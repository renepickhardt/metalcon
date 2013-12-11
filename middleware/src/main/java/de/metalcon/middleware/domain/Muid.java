package de.metalcon.middleware.domain;

import de.metalcon.middleware.domain.entity.EntityType;

public class Muid {
    
    private long muid;
    
    public Muid(long muid) {
        this.muid = muid;
    }
    
    public long getMuid() {
        return muid;
    }
    
    public void setMuid(long muid) {
        this.muid = muid;
    }
    
    public EntityType getEntityType() {
        // TODO: stub
        long rem = muid % 10;
        if (rem == 1)
            return EntityType.BAND;
        else if (rem == 2)
            return EntityType.RECORD;
        else if (rem == 3)
            return EntityType.TRACK;
        
        throw new UnsupportedOperationException(
                "Muid.getEntityType() not implemented yet.");
    }

}
