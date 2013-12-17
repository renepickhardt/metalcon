package de.metalcon.middleware.controller.entity.impl.user;

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
import de.metalcon.middleware.controller.entity.generating.RecordsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.ReviewsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.TracksTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generating.VenuesTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.BandsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.PhotosTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecordsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.ReviewsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.TracksTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.controller.entity.generator.VenuesTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.USER_MAPPING,
        method = RequestMethod.GET)
public class UserController extends EntityController implements
        AboutTabGenerating, BandsTabGenerating, EventsTabGenerating,
        NewsfeedTabGenerating, PhotosTabGenerating,
        RecommendationsTabGenerating, RecordsTabGenerating,
        ReviewsTabGenerating, TracksTabGenerating, UsersTabGenerating,
        VenuesTabGenerating {

    @Autowired
    private UserAboutTabGenerator aboutTabGenerator;

    @Autowired
    private UserBandsTabGenerator bandsTabGenerator;

    @Autowired
    private UserEventsTabGenerator eventsTabGenerator;

    @Autowired
    private UserNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private UserPhotosTabGenerator photosTabGenerator;

    @Autowired
    private UserRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private UserRecordsTabGenerator recordsTabGenerator;

    @Autowired
    private UserReviewsTabGenerator reviewsTabGenerator;

    @Autowired
    private UserTracksTabGenerator tracksTabGenerator;

    @Autowired
    private UserUsersTabGenerator usersTabGenerator;

    @Autowired
    private UserVenuesTabGenerator venuesTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
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
    public RecordsTabGenerator getRecordsTabGenerator() {
        return recordsTabGenerator;
    }

    @Override
    public ReviewsTabGenerator getReviewsTabGenerator() {
        return reviewsTabGenerator;
    }

    @Override
    public TracksTabGenerator getTracksTabGenerator() {
        return tracksTabGenerator;
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
