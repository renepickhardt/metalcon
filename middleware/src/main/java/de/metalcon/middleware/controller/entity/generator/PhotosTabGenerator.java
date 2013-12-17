package de.metalcon.middleware.controller.entity.generator;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

@Component
public abstract class PhotosTabGenerator extends EntityTabGenerator {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.PHOTOS_TAB;
    }

}
