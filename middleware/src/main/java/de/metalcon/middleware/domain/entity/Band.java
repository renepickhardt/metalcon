package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Muid;

public class Band extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.BAND;
    }
    
    public Band(Muid muid, String name) {
        super(muid, name);
    }
    
}
