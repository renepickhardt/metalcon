package de.metalcon.middleware.controller.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;
import de.metalcon.middleware.views.tabs.entity.EntityTabType;

public abstract class EntityController extends MetalconController {

    @Autowired
    protected EntityUrlMapper urlMapper;
    
    public abstract EntityType getEntityType();
    
    protected EntityTabType getDefaultTab() {
        return EntityTabType.NEWSFEED_TAB;
    }
    
    private final ModelAndView handleTab(EntityTabType entityTabType,
            String path1, String path2, String path3)
    throws RedirectException {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();
        
        Muid muid = urlMapper.getMuid(getEntityType(), path1, path2, path3);
        
        ModelAndView mv = null;
        switch (entityTabType) {
            case INFO_TAB:            mv = handleInfoTab(muid);            break;
            case NEWSFEED_TAB:        mv = handleNewsfeedTab(muid);        break;
            case BANDS_TAB:           mv = handleBandsTab(muid);           break;
            case RECORDS_TAB:         mv = handleRecordsTab(muid);         break;
            case TRACKS_TAB:          mv = handleTracksTab(muid);          break;
            case REVIEWS_TAB:         mv = handleReviewsTab(muid);         break;
            case VENUES_TAB:          mv = handleVenuesTab(muid);          break;
            case EVENTS_TAB:          mv = handleEventsTab(muid);          break;
            case USERS_TAB:           mv = handleUsersTab(muid);           break;
            case PHOTOS_TAB:          mv = handlePhotosTab(muid);          break;
            case RECOMMENDATIONS_TAB: mv = handleRecommendationsTab(muid); break;
            
            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabType.toString() + ".");
        }
        
        return mv;
    }
    
    protected abstract ModelAndView handleInfoTab(Muid muid);
    
    protected abstract ModelAndView handleNewsfeedTab(Muid muid);
    
    protected abstract ModelAndView handleBandsTab(Muid muid);
    
    protected abstract ModelAndView handleRecordsTab(Muid muid);
    
    protected abstract ModelAndView handleTracksTab(Muid muid);
    
    protected abstract ModelAndView handleReviewsTab(Muid muid);
    
    protected abstract ModelAndView handleVenuesTab(Muid muid);
    
    protected abstract ModelAndView handleEventsTab(Muid muid);
    
    protected abstract ModelAndView handleUsersTab(Muid muid);
    
    protected abstract ModelAndView handlePhotosTab(Muid muid);
    
    protected abstract ModelAndView handleRecommendationsTab(Muid muid);
    
    @RequestMapping(EntityUrlMapper.EMPTY_TAB_MAPPING)
    public final ModelAndView mappingEmptyTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.EMPTY_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.INFO_TAB_MAPPING)
    public final ModelAndView mappingInfoTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.INFO_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.NEWSFEED_TAB_MAPPING)
    public final ModelAndView mappingNewsfeedTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.NEWSFEED_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.BANDS_TAB_MAPPING)
    public final ModelAndView mappingBandsTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.BANDS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.RECORDS_TAB_MAPPING)
    public final ModelAndView mappingRecordsTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.RECORDS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.TRACKS_TAB_MAPPING)
    public final ModelAndView mappingTracksTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.TRACKS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.REVIEWS_TAB_MAPPING)
    public final ModelAndView mappingReviewsTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.REVIEWS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.VENUES_TAB_MAPPING)
    public final ModelAndView mappingVenuesTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.VENUES_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.EVENTS_TAB_MAPPING)
    public final ModelAndView mappingEventsTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.EVENTS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.USERS_TAB_MAPPING)
    public final ModelAndView mappingUsersTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.USERS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.PHOTOS_TAB_MAPPING)
    public final ModelAndView mappingPhotosTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.PHOTOS_TAB, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.RECOMMENDATIONS_TAB_MAPPING)
    public final ModelAndView mappingRecommendationsTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        return handleTab(EntityTabType.RECOMMENDATIONS_TAB, path1, path2, path3);
    }
   
}