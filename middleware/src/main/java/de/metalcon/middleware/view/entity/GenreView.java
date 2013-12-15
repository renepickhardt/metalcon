package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;

public class GenreView extends EntityView {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.GENRE;
    }
    
}
