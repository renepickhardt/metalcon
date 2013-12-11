package de.metalcon.middleware.domain.entity;

public class Genre extends Entity {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.GENRE;
    }
    
    public Genre(String name) {
        super(name);
    }
    
}
