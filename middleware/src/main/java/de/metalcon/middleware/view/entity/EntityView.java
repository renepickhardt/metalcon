package de.metalcon.middleware.view.entity;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.MetalconView;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public abstract class EntityView extends MetalconView {

    private Muid muid;

    private EntityTabContent entityTabContent;

    private Map<EntityTabType, EntityTabPreview> entityTabPreviews;

    public abstract EntityType getEntityType();

    public EntityView() {
        super();
        muid = null;
        entityTabContent = null;
        entityTabPreviews = null;
    }

    public final long getMuid() {
        return muid.getValue();
    }

    public final void setMuid(Muid muid) {
        this.muid = muid;
    }

    public final EntityTabContent getEntityTabContent() {
        return entityTabContent;
    }

    public final void setEntityTabContent(EntityTabContent entityTab) {
        entityTabContent = entityTab;
    }

    public final Map<String, EntityTabPreview> getEntityTabPreviews() {
        Map<String, EntityTabPreview> m =
                new HashMap<String, EntityTabPreview>();
        for (Map.Entry<EntityTabType, EntityTabPreview> entityTabPreview : entityTabPreviews
                .entrySet())
            m.put(entityTabPreview.getKey().toString(),
                    entityTabPreview.getValue());
        return m;
    }

    public final void setEntityTabPreviews(
            Map<EntityTabType, EntityTabPreview> entityTabPreviews) {
        this.entityTabPreviews = entityTabPreviews;
    }

}
