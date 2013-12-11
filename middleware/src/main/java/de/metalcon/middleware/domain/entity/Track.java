package de.metalcon.middleware.domain.entity;

public class Track extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }
    
    private Band band;
    
    private Record record;
    
    public Track(String name) {
        super(name);
        band   = null;
        record = null;
    }
    
    public Band getBand() {
        return band;
    }
    
    public void setBand(Band band) {
        this.band = band;
    }
    
    public Record getRecord() {
        return record;
    }
    
    public void setRecord(Record record) {
        this.record = record;
    }

}
