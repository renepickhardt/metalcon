package de.metalcon.middleware.view.entity.tab.preview;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public class NewsfeedTabPreview extends EntityTabPreview {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.NEWSFEED_TAB;
    }

}
