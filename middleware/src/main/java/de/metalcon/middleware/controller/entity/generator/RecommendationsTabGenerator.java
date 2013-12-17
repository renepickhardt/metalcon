package de.metalcon.middleware.controller.entity.generator;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

@Component
public abstract class RecommendationsTabGenerator extends EntityTabGenerator {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.RECOMMENDATIONS_TAB;
    }

}
