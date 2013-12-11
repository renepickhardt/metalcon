package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Muid;

public class Instrument extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.INSTRUMENT;
    }
    
    public Instrument(Muid muid, String name) {
        super(muid, name);
    }
    
}
