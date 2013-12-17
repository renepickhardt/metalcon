package de.metalcon.middleware.controller.entity.impl.track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.EntityController;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generator.AboutTabGenerator;
import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;
import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;
import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(
        value = UrlMappings.TRACK_MAPPING,
        method = RequestMethod.GET)
public class TrackController extends EntityController implements
        AboutTabGenerating, NewsfeedTabGenerating,
        RecommendationsTabGenerating, UsersTabGenerating {

    @Autowired
    private TrackAboutTabGenerator aboutTabGenerator;

    @Autowired
    private TrackNewsfeedTabGenerator newsfeedTabGenerator;

    @Autowired
    private TrackRecommendationsTabGenerator recommendationsTabGenerator;

    @Autowired
    private TrackUsersTabGenerator usersTabGenerator;

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
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
    public UsersTabGenerator getUsersTabGenerator() {
        return usersTabGenerator;
    }

}
