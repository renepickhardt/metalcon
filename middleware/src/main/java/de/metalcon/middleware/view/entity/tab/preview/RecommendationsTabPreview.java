package de.metalcon.middleware.view.entity.tab.preview;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public class RecommendationsTabPreview extends EntityTabPreview {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.RECOMMENDATIONS_TAB;
    }

}
