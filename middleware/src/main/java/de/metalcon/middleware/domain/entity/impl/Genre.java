package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class Genre extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.GENRE;
    }

    public Genre(
            Muid muid,
            String name) {
        super(muid, name);
    }

}
