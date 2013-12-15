package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.tab.EntityTabType;

@Controller
@RequestMapping(value  = UrlMappings.BAND_MAPPING,
                method = RequestMethod.GET)
public class BandController extends EntityController {

    @Override
    public EntityType getEntityType() {
        return EntityType.BAND;
    }
    
    public BandController() {
        super();
        registerTab(EntityTabType.ABOUT_TAB);
        registerTab(EntityTabType.NEWSFEED_TAB);
        registerTab(EntityTabType.RECORDS_TAB);
        registerTab(EntityTabType.TRACKS_TAB);
        registerTab(EntityTabType.EVENTS_TAB);
        registerTab(EntityTabType.USERS_TAB);
        registerTab(EntityTabType.PHOTOS_TAB);
        registerTab(EntityTabType.RECOMMENDATIONS_TAB);
    }

}
