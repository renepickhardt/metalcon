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
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Muid o = (Muid) other;
        return muid == o.muid;
    }
    
    @Override
    public int hashCode() {
        int hash = 9823;
        int mult = 887;
        
        hash = hash*mult + ((Long) muid).hashCode();
        
        return hash;
    }

}
