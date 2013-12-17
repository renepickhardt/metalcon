package de.metalcon.middleware.domain.entity;

import de.metalcon.middleware.domain.Muid;

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
