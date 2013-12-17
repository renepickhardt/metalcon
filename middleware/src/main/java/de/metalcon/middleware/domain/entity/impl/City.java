package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class City extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.CITY;
    }

    public City(
            Muid muid,
            String name) {
        super(muid, name);
    }

}
