package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.views.tabs.entity.EntityTabType;

@Controller
@RequestMapping(value  = EntityUrlMapper.TRACK_MAPPING,
                method = RequestMethod.GET)
public class TrackController extends EntityController {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }
    
    public TrackController() {
        super();
        registerTab(EntityTabType.INFO_TAB);
        registerTab(EntityTabType.NEWSFEED_TAB);
        registerTab(EntityTabType.USERS_TAB);
        registerTab(EntityTabType.RECOMMENDATIONS_TAB);
    }
    
}
