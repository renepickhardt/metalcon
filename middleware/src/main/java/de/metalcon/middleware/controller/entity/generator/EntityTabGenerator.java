package de.metalcon.middleware.controller.entity.generator;

import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public abstract class EntityTabGenerator {

    public abstract EntityTabType getEntityTabType();

    public abstract void generateTabContent(
            EntityTabContent tabContent,
            Entity entity);

    public abstract void genereteTabPreview(
            EntityTabPreview tabPreview,
            Entity entity);

}
