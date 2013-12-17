package de.metalcon.middleware.controller.entity.impl.tour;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.EntityController;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.TOUR_MAPPING,
        method = RequestMethod.GET)
public class TourController extends EntityController implements
        AboutTabGenerating, NewsfeedTabGenerating {

    @Autowired
    private TourAboutTabGenerator aboutTabGenerator;

    @Autowired
    private TourNewsfeedTabGenerator newsfeedTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.TOUR;
    }

    @Override
    public AboutTabGenerator getAboutTabGenerator() {
        return aboutTabGenerator;
    }

    @Override
    public NewsfeedTabGenerator getNewsfeedTabGenerator() {
        return newsfeedTabGenerator;
    }

}
