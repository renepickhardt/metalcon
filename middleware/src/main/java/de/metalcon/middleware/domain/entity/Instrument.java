package de.metalcon.middleware.domain.entity;

public class Instrument extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.INSTRUMENT;
    }
    
    public Instrument(String name) {
        super(name);
    }
    
}
