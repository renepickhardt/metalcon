package de.metalcon.middleware.controller.entity.impl.genre;

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
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecordsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.ReviewsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.TracksTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.BandsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecordsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.ReviewsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.TracksTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.GENRE_MAPPING,
        method = RequestMethod.GET)
public class GenreController extends EntityController implements
        AboutTabGenerating, BandsTabGenerating, EventsTabGenerating,
        NewsfeedTabGenerating, RecommendationsTabGenerating,
        RecordsTabGenerating, ReviewsTabGenerating, TracksTabGenerating,
        UsersTabGenerating {

    @Autowired
    private GenreAboutTabGenerator aboutTabGenerator;

    @Autowired
    private GenreBandsTabGenerator bandsTabGenerator;

    @Autowired
    private GenreEventsTabGenerator eventsTabGenerator;

    @Autowired
    private GenreNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private GenreRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private GenreRecordsTabGenerator recordsTabGenerator;

    @Autowired
    private GenreReviewsTabGenerator reviewsTabGenerator;

    @Autowired
    private GenreTracksTabGenerator tracksTabGenerator;

    @Autowired
    private GenreUsersTabGenerator usersTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.GENRE;
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

}
