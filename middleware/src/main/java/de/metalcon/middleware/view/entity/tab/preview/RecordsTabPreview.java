package de.metalcon.middleware.view.entity.tab.preview;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public class RecordsTabPreview extends EntityTabPreview {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.RECORDS_TAB;
    }

}
