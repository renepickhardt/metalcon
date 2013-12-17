package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class Record extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.RECORD;
    }

    private Muid band;

    private Integer releaseYear;

    public Record(
            Muid muid,
            String name) {
        super(muid, name);
        band = null;
        releaseYear = null;
    }

    public Muid getBand() {
        return band;
    }

    public void setBand(Muid band) {
        this.band = band;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

}
