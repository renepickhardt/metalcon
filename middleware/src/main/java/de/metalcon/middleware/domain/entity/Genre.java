package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Muid;

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
