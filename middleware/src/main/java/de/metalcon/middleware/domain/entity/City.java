package de.metalcon.middleware.domain.entity;

public class City extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.CITY;
    }
    
    public City(String name) {
        super(name);
    }
    
}
