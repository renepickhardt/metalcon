package de.metalcon.middleware.controller.entity.impl.city;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.EntityController;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.BandsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.EventsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generating.PhotosTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generating.VenuesTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.BandsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.PhotosTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.controller.entity.generator.VenuesTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.CITY_MAPPING,
        method = RequestMethod.GET)
public class CityController extends EntityController implements
        AboutTabGenerating, BandsTabGenerating, EventsTabGenerating,
        NewsfeedTabGenerating, PhotosTabGenerating,
        RecommendationsTabGenerating, UsersTabGenerating, VenuesTabGenerating {

    @Autowired
    private CityAboutTabGenerator aboutTabGenerator;

    @Autowired
    private CityBandsTabGenerator bandsTabGenerator;

    @Autowired
    private CityEventsTabGenerator eventsTabGenerator;

    @Autowired
    private CityNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private CityPhotosTabGenerator photosTabGenerator;

    @Autowired
    private CityRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private CityUsersTabGenerator usersTabGenerator;

    @Autowired
    private CityVenuesTabGenerator venuesTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.CITY;
    }

    @Override
    public AboutTabGenerator getAboutTabGenerator() {
        return aboutTabGenerator;
    }

    @Override
    public BandsTabGenerator getBandsTabGenerator() {
        return bandsTabGenerator;
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

    @Override
    public VenuesTabGenerator getVenuesTabGenerator() {
        return venuesTabGenerator;
    }

}
