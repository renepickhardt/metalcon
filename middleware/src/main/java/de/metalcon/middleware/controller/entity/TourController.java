package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.tab.EntityTabType;

@Controller
@RequestMapping(value  = UrlMappings.TOUR_MAPPING,
                method = RequestMethod.GET)
public class TourController extends EntityController {

    @Override
    public EntityType getEntityType() {
        return EntityType.TOUR;
    }
    
    public TourController() {
        super();
        registerTab(EntityTabType.ABOUT_TAB);
        registerTab(EntityTabType.NEWSFEED_TAB);
    }

}
