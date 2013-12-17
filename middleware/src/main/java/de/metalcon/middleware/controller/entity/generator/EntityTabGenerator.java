package de.metalcon.middleware.controller.entity.generator;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public abstract class EntityTabGenerator {

    public abstract EntityTabType getEntityTabType();

    public abstract void generateTabContent(
            EntityTabContent tabContent,
            Muid muid);

    public abstract void genereteTabPreview(
            EntityTabPreview tabPreview,
            Muid muid);

}
