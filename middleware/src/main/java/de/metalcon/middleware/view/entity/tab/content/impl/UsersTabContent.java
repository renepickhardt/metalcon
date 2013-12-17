package de.metalcon.middleware.view.entity.tab.content.impl;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;

public class UsersTabContent extends EntityTabContent {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.USERS_TAB;
    }

}
