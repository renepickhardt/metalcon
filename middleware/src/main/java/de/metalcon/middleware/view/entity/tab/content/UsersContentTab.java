package de.metalcon.middleware.view.entity.tab.content;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public class UsersContentTab extends EntityTabContent {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.USERS_TAB;
    }

}
