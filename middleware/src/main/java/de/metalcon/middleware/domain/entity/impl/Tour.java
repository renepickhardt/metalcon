package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class Tour extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.TOUR;
    }

    public Integer year;

    public Tour(
            Muid muid,
            String name) {
        super(muid, name);
        year = null;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

}
