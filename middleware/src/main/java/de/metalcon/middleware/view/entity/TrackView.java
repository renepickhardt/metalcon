package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;

public class TrackView extends EntityView {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }
    
    public TrackView() throws Exception {
        super();
    }

}
