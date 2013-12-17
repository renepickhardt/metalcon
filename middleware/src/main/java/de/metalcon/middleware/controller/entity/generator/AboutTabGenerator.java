package de.metalcon.middleware.controller.entity.generator;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

@Component
public abstract class AboutTabGenerator extends EntityTabGenerator {

    @Override
    public EntityTabType getEntityTabType() {
        return EntityTabType.ABOUT_TAB;
    }

}
