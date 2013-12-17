package de.metalcon.middleware.controller.entity.impl.record;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.EntityController;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.ReviewsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.TracksTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.ReviewsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.TracksTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.RECORD_MAPPING,
        method = RequestMethod.GET)
public class RecordController extends EntityController implements
        AboutTabGenerating, NewsfeedTabGenerating,
        RecommendationsTabGenerating, ReviewsTabGenerating,
        TracksTabGenerating, UsersTabGenerating {

    @Autowired
    private RecordAboutTabGenerator aboutTabGenerator;

    @Autowired
    private RecordNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private RecordRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private RecordReviewsTabGenerator reviewsTabGenerator;

    @Autowired
    private RecordTracksTabGenerator tracksTabGenerator;

    @Autowired
    private RecordUsersTabGenerator usersTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.RECORD;
    }

    @Override
    public AboutTabGenerator getAboutTabGenerator() {
        return aboutTabGenerator;
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
