package de.metalcon.middleware.view.entity.tab.preview.impl;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public class PhotosTabPreview extends EntityTabPreview {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.PHOTOS_TAB;
    }

}
