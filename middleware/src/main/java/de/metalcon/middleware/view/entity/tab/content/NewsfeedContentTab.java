package de.metalcon.middleware.view.entity.tab.content;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public class NewsfeedContentTab extends EntityTabContent {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.NEWSFEED_TAB;
    }

}
