package de.metalcon.middleware.controller.entity.impl.venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.EntityController;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.EventsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generating.PhotosTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.PhotosTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.VENUE_MAPPING,
        method = RequestMethod.GET)
public class VenueController extends EntityController implements
        AboutTabGenerating, EventsTabGenerating, NewsfeedTabGenerating,
        PhotosTabGenerating, RecommendationsTabGenerating, UsersTabGenerating {

    @Autowired
    private VenueAboutTabGenerator aboutTabGenerator;

    @Autowired
    private VenueEventsTabGenerator eventsTabGenerator;

    @Autowired
    private VenueNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private VenuePhotosTabGenerator photosTabGenerator;

    @Autowired
    private VenueRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private VenueUsersTabGenerator usersTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.VENUE;
    }

    @Override
    public AboutTabGenerator getAboutTabGenerator() {
        return aboutTabGenerator;
    }

    @Override
    public EventsTabGenerator getEventsTabGenerator() {
        return eventsTabGenerator;
    }

    @Override
    public NewsfeedTabGenerator getNewsfeedTabGenerator() {
        return newsfeedTabGenerator;
    }

    @Override
    public PhotosTabGenerator getPhotosTabGenerator() {
        return photosTabGenerator;
    }

    @Override
    public RecommendationsTabGenerator getRecommendationsTabGenerator() {
        return recommendationsTabGenerator;
    }

    @Override
    public UsersTabGenerator getUsersTabGenerator() {
        return usersTabGenerator;
    }

}
