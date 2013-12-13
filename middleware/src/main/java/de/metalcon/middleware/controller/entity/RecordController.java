package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.views.tabs.entity.EntityTabType;

@Controller
@RequestMapping(value  = EntityUrlMapper.RECORD_MAPPING,
                method = RequestMethod.GET)
public class RecordController extends EntityController {

    @Override
    public EntityType getEntityType() {
        return EntityType.RECORD;
    }
    
    public RecordController() {
        super();
        registerTab(EntityTabType.INFO_TAB);
        registerTab(EntityTabType.NEWSFEED_TAB);
        registerTab(EntityTabType.TRACKS_TAB);
        registerTab(EntityTabType.REVIEWS_TAB);
        registerTab(EntityTabType.USERS_TAB);
        registerTab(EntityTabType.RECOMMENDATIONS_TAB);
    }
    
}
