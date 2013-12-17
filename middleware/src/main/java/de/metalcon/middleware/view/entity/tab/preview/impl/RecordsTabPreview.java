package de.metalcon.middleware.view.entity.tab.preview.impl;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public class RecordsTabPreview extends EntityTabPreview {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.RECORDS_TAB;
    }

}
