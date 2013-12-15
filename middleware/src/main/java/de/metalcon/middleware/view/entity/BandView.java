package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;

public class BandView extends EntityView {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.BAND;
    }
    
    public BandView() throws Exception {
        super();
    }

}
