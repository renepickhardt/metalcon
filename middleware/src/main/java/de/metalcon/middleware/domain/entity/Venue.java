package de.metalcon.middleware.domain.entity;

public class Venue extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.VENUE;
    }
    
    private City city;
    
    public Venue(String name) {
        super(name);
        city = null;
    }
    
    public City getCity() {
        return city;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    
}
