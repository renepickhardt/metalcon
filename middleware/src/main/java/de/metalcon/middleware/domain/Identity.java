package de.metalcon.middleware.domain;

public abstract class Identity {
    
    private Muid muid;
    
    public Identity(Muid muid) {
        setMuid(muid);
    }
    
    public Muid getMuid() {
        return muid;
    }
    
    public void setMuid(Muid muid) {
        this.muid = muid;
    }

}
