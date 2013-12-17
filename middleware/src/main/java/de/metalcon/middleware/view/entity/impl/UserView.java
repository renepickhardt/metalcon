package de.metalcon.middleware.view.entity.impl;

import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.EntityView;

public class UserView extends EntityView {

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }

}
