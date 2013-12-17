package de.metalcon.middleware.controller.entity.impl.band;

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
import de.metalcon.middleware.controller.entity.generating.RecordsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.TracksTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.PhotosTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecordsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.TracksTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.BAND_MAPPING,
        method = RequestMethod.GET)
public class BandController extends EntityController implements
        AboutTabGenerating, EventsTabGenerating, NewsfeedTabGenerating,
        PhotosTabGenerating, RecommendationsTabGenerating,
        RecordsTabGenerating, TracksTabGenerating, UsersTabGenerating {

    @Autowired
    private BandAboutTabGenerator aboutTabGenerator;

    @Autowired
    private BandEventsTabGenerator eventsTabGenerator;

    @Autowired
    private BandNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private BandPhotosTabGenerator photosTabGenerator;

    @Autowired
    private BandRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private BandRecordsTabGenerator recordsTabGenerator;

    @Autowired
    private BandTracksTabGenerator tracksTabGenerator;

    @Autowired
    private BandUsersTabGenerator usersTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.BAND;
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
    public RecordsTabGenerator getRecordsTabGenerator() {
        return recordsTabGenerator;
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
