package de.metalcon.middleware.view.entity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.tab.EntityTab;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;

public abstract class EntityView implements View {
    
    private Muid muid;
    
    private EntityTab entityTab;
    
    private Map<EntityTabType, EntityTabPreview> entityTabPreviews;
    
    private View view;
    
    @Autowired
    private ViewResolver viewResolver;
    
    public abstract EntityType getEntityType();
    
    public EntityView() {
        muid              = null;
        entityTab         = null;
        entityTabPreviews = null;
        view              = null;
    }
    
    @PostConstruct
    private void init() throws Exception {
        view = viewResolver.resolveViewName(
                "entity/" + getEntityType().toString().toLowerCase(),
                Locale.GERMANY);
    }
    
    public final String getContentType() {
        return view.getContentType();
    }
    
    public final void render(Map<String, ?> model,
            HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Map<String, Object> m = new HashMap<String, Object>(model);
        m.put("view", this);
        view.render(m, request, response);
    }
    
    public final Muid getMuid() {
        return muid;
    }
    
    public final void setMuid(Muid muid) {
        this.muid = muid;
    }
    
    public final EntityTab getEntityTab() {
        return entityTab;
    }
    
    public final void setEntityTab(EntityTab entityTab) {
        this.entityTab = entityTab;
    }
    
    public final EntityTabPreview getEntityTabPreview(EntityTabType entityTabType) {
        return entityTabPreviews.get(entityTabType);
    }
    
    public final Map<EntityTabType, EntityTabPreview> getEntityTabPreviews() {
        return entityTabPreviews;
    }
    
    public final void setEntityTabPreviews(
            Map<EntityTabType, EntityTabPreview> entityTabPreviews) {
        this.entityTabPreviews = entityTabPreviews;
    }
    
}
