package de.metalcon.middleware.controller.entity;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;
import de.metalcon.middleware.views.tabs.entity.BandsTab;
import de.metalcon.middleware.views.tabs.entity.EntityTab;
import de.metalcon.middleware.views.tabs.entity.EntityTabType;
import de.metalcon.middleware.views.tabs.entity.EventsTab;
import de.metalcon.middleware.views.tabs.entity.InfoTab;
import de.metalcon.middleware.views.tabs.entity.NewsfeedTab;
import de.metalcon.middleware.views.tabs.entity.PhotosTab;
import de.metalcon.middleware.views.tabs.entity.RecommendationsTab;
import de.metalcon.middleware.views.tabs.entity.RecordsTab;
import de.metalcon.middleware.views.tabs.entity.ReviewsTab;
import de.metalcon.middleware.views.tabs.entity.TracksTab;
import de.metalcon.middleware.views.tabs.entity.UsersTab;
import de.metalcon.middleware.views.tabs.entity.VenuesTab;

public abstract class EntityController extends MetalconController {

    @Autowired
    protected EntityUrlMapper urlMapper;
    
    
    public abstract EntityType getEntityType();
    
    protected EntityTabType getDefaultTab() {
        return EntityTabType.NEWSFEED_TAB;
    }
    
    private Set<EntityTabType> entityTabs;
    
    public EntityController() {
        entityTabs = new HashSet<EntityTabType>();
    }
    
    protected final void registerTab(EntityTabType entityTabType) {
        entityTabs.add(entityTabType);
    }
    
    private final ModelAndView handleTab(
            EntityTabType entityTabType,
            HttpServletRequest request,
            String path1, String path2, String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();
        
        Muid muid = urlMapper.getMuid(getEntityType(), path1, path2, path3);
        
        if (!entityTabs.contains(entityTabType) || muid == null)
            throw new NoSuchRequestHandlingMethodException(request);
        
        EntityTab entityTab;
        switch (entityTabType) {
            case INFO_TAB:
                InfoTab infoTab = new InfoTab();
                handleInfoTab(infoTab, muid);
                entityTab = infoTab;
                break;
            case NEWSFEED_TAB:
                NewsfeedTab newsfeedTab = new NewsfeedTab();
                handleNewsfeedTab(newsfeedTab, muid);
                entityTab = newsfeedTab;
                break;
            case BANDS_TAB:
                BandsTab bandsTab = new BandsTab();
                handleBandsTab(bandsTab, muid);
                entityTab = bandsTab;
                break;
            case RECORDS_TAB:
                RecordsTab recordsTab = new RecordsTab();
                handleRecordsTab(recordsTab, muid);
                entityTab = recordsTab;
                break;
            case TRACKS_TAB:
                TracksTab tracksTab = new TracksTab();
                handleTracksTab(tracksTab, muid);
                entityTab = tracksTab;
                break;
            case REVIEWS_TAB:
                ReviewsTab reviewsTab = new ReviewsTab();
                handleReviewsTab(reviewsTab, muid);
                entityTab = reviewsTab;
                break;
            case VENUES_TAB:
                VenuesTab venuesTab = new VenuesTab();
                handleVenuesTab(venuesTab, muid);
                entityTab = venuesTab;
                break;
            case EVENTS_TAB:
                EventsTab eventsTab = new EventsTab();
                handleEventsTab(eventsTab, muid);
                entityTab = eventsTab;
                break;
            case USERS_TAB:
                UsersTab usersTab = new UsersTab();
                handleUsersTab(usersTab, muid);
                entityTab = usersTab;
                break;
            case PHOTOS_TAB:
                PhotosTab photosTab = new PhotosTab();
                handlePhotosTab(photosTab, muid);
                entityTab = photosTab;
                break;
            case RECOMMENDATIONS_TAB:
                RecommendationsTab recommendationsTab = new RecommendationsTab();
                handleRecommendationsTab(recommendationsTab, muid);
                entityTab = recommendationsTab;
                break;

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabType.toString() + ".");
        }
        
        return null;
    }
    
    protected void handleInfoTab(InfoTab tab, Muid muid) {
    }
    
    protected void handleNewsfeedTab(NewsfeedTab tab, Muid muid) {
    }
    
    protected void handleBandsTab(BandsTab tab, Muid muid) {
    }
    
    protected void handleRecordsTab(RecordsTab tab, Muid muid) {
    }
    
    protected void handleTracksTab(TracksTab tab, Muid muid) {
    }
    
    protected void handleReviewsTab(ReviewsTab tab, Muid muid) {
    }
    
    protected void handleVenuesTab(VenuesTab tab, Muid muid) {
    }
    
    protected void handleEventsTab(EventsTab tab, Muid muid) {
    }
    
    protected void handleUsersTab(UsersTab tab, Muid muid) {
    }
   
    protected void handlePhotosTab(PhotosTab tab, Muid muid) {
    }
    
    protected void handleRecommendationsTab(RecommendationsTab tab, Muid muid) {
    }
    
    @RequestMapping(EntityUrlMapper.EMPTY_TAB_MAPPING)
    public final ModelAndView mappingEmptyTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EMPTY_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.INFO_TAB_MAPPING)
    public final ModelAndView mappingInfoTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.INFO_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.NEWSFEED_TAB_MAPPING)
    public final ModelAndView mappingNewsfeedTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.NEWSFEED_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.BANDS_TAB_MAPPING)
    public final ModelAndView mappingBandsTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.BANDS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.RECORDS_TAB_MAPPING)
    public final ModelAndView mappingRecordsTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECORDS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.TRACKS_TAB_MAPPING)
    public final ModelAndView mappingTracksTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.TRACKS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.REVIEWS_TAB_MAPPING)
    public final ModelAndView mappingReviewsTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.REVIEWS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.VENUES_TAB_MAPPING)
    public final ModelAndView mappingVenuesTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.VENUES_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.EVENTS_TAB_MAPPING)
    public final ModelAndView mappingEventsTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EVENTS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.USERS_TAB_MAPPING)
    public final ModelAndView mappingUsersTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.USERS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.PHOTOS_TAB_MAPPING)
    public final ModelAndView mappingPhotosTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.PHOTOS_TAB, request, path1, path2, path3);
    }
   
    @RequestMapping(EntityUrlMapper.RECOMMENDATIONS_TAB_MAPPING)
    public final ModelAndView mappingRecommendationsTab(
            HttpServletRequest request,
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECOMMENDATIONS_TAB, request, path1, path2, path3);
    }
   
}