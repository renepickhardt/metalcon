package de.metalcon.middleware.view.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.entity.EntityType;

@Component
@Scope("prototype")
public class BandView extends EntityView {
    
    @Override
    public EntityType getEntityType() {
        return EntityType.BAND;
    }
    
    public BandView() throws Exception {
        super();
    }

}
