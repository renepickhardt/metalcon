package de.metalcon.middleware.domain.entity.impl;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

public class Track extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }

    private Muid band;

    private Muid record;

    private Integer trackNumber;

    public Track(
            Muid muid,
            String name) {
        super(muid, name);
        band = null;
        record = null;
    }

    public Muid getBand() {
        return band;
    }

    public void setBand(Muid band) {
        this.band = band;
    }

    public Muid getRecord() {
        return record;
    }

    public void setRecord(Muid record) {
        this.record = record;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

}
