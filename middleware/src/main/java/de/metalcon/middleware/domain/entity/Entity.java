package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Identity;
import de.metalcon.middleware.domain.Muid;

public abstract class Entity extends Identity {
    
    public abstract EntityType getEntityType();
    
    private String name;
    
    public Entity(Muid muid, String name) {
        super(muid);
        setName(name);
    }
    
    public final String getName() {
        return name;
    }
    
    public final void setName(String name) {
        this.name = name;
    }

}
