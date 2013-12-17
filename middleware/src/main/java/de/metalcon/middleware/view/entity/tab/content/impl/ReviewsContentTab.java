package de.metalcon.middleware.view.entity.tab.content.impl;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;

public class ReviewsContentTab extends EntityTabContent {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.REVIEWS_TAB;
    }

}
