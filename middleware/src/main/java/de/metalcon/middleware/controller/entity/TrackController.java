package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(value  = EntityUrlMapper.TRACK_MAPPING,
                method = RequestMethod.GET)
public class TrackController extends EntityController {

    @Override
    public EntityType getEntityType() {
        return EntityType.TRACK;
    }

    @Override
    protected ModelAndView handleInfoTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleNewsfeedTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleBandsTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleRecordsTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleTracksTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleReviewsTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleVenuesTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleEventsTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleUsersTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handlePhotosTab(Muid muid) {
        return null;
    }

    @Override
    protected ModelAndView handleRecommendationsTab(Muid muid) {
        return null;
    }
    
}
