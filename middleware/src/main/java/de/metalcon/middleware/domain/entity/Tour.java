package de.metalcon.middleware.domain.entity;

public class Tour extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.TOUR;
    }
    
    public Integer year;
    
    public Tour(String name) {
        super(name);
        year = null;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
}
