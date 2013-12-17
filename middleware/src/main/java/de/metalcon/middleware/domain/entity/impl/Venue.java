package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class Venue extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.VENUE;
    }

    private Muid city;

    public Venue(
            Muid muid,
            String name) {
        super(muid, name);
        city = null;
    }

    public Muid getCity() {
        return city;
    }

    public void setCity(Muid city) {
        this.city = city;
    }

}
