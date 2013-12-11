package de.metalcon.middleware.domain.entity;

public class Record extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.RECORD;
    }
    
    private Band band;
    
    private Integer releaseYear;

    public Record(String name) {
        super(name);
        band        = null;
        releaseYear = null;
    }
    
    public Band getBand() {
        return band;
    }
    
    public void setBand(Band band) {
        this.band = band;
    }
    
    public Integer getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
    
}
