package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Muid;

public class Track extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }
    
    private Muid band;
    
    private Muid record;
    
    public Track(String name) {
        super(name);
        band   = null;
        record = null;
    }
    
    public Muid getBand() {
        return band;
    }
    
    public void setBand(Muid band) {
        this.band = band;
    }
    
    public Muid getRecord() {
        return record;
    }
    
    public void setRecord(Muid record) {
        this.record = record;
    }

}
