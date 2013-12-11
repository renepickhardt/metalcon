package de.metalcon.middleware.domain;

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
        return EntityType.BAND;
    }

}
