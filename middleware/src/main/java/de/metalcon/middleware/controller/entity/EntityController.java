package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import de.metalcon.middleware.view.entity.tab.BandsTab;
import de.metalcon.middleware.view.entity.tab.EntityTab;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.EventsTab;
import de.metalcon.middleware.view.entity.tab.InfoTab;
import de.metalcon.middleware.view.entity.tab.NewsfeedTab;
import de.metalcon.middleware.view.entity.tab.PhotosTab;
import de.metalcon.middleware.view.entity.tab.RecommendationsTab;
import de.metalcon.middleware.view.entity.tab.RecordsTab;
import de.metalcon.middleware.view.entity.tab.ReviewsTab;
import de.metalcon.middleware.view.entity.tab.TracksTab;
import de.metalcon.middleware.view.entity.tab.UsersTab;
import de.metalcon.middleware.view.entity.tab.VenuesTab;
import de.metalcon.middleware.view.entity.tab.preview.BandsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.EventsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.InfoTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.NewsfeedTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.PhotosTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.RecommendationsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.RecordsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.ReviewsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.TracksTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.UsersTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.VenuesTabPreview;

public abstract class EntityController extends MetalconController {

    @Autowired
    protected EntityUrlMapper urlMapper;
    
    private Set<EntityTabType> entityTabs;
    
    public abstract EntityType getEntityType();
    
    protected EntityTabType getDefaultTab() {
        return EntityTabType.NEWSFEED_TAB;
    }
    
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
        
        Map<EntityTabType, EntityTabPreview> entityTabPreviews =
                new HashMap<EntityTabType, EntityTabPreview>();
        for (EntityTabType entityTabPreviewType : entityTabs) {
            EntityTabPreview entityTabPreview =
                    generateTabPreview(entityTabPreviewType, muid);
            entityTabPreviews.put(entityTabPreviewType, entityTabPreview);
        }
        
        EntityTab entityTab = generateTab(entityTabType, muid);
                
        return null;
    }
    
    private EntityTab generateTab(
            EntityTabType entityTabType, Muid muid) {
        switch (entityTabType) {
            case INFO_TAB:
                InfoTab infoTab = new InfoTab();
                generateInfoTab(infoTab, muid);
                return infoTab;
            case NEWSFEED_TAB:
                NewsfeedTab newsfeedTab = new NewsfeedTab();
                generateNewsfeedTab(newsfeedTab, muid);
                return newsfeedTab;
            case BANDS_TAB:
                BandsTab bandsTab = new BandsTab();
                generateBandsTab(bandsTab, muid);
                return bandsTab;
            case RECORDS_TAB:
                RecordsTab recordsTab = new RecordsTab();
                generateRecordsTab(recordsTab, muid);
                return recordsTab;
            case TRACKS_TAB:
                TracksTab tracksTab = new TracksTab();
                generateTracksTab(tracksTab, muid);
                return tracksTab;
            case REVIEWS_TAB:
                ReviewsTab reviewsTab = new ReviewsTab();
                generateReviewsTab(reviewsTab, muid);
                return reviewsTab;
            case VENUES_TAB:
                VenuesTab venuesTab = new VenuesTab();
                generateVenuesTab(venuesTab, muid);
                return venuesTab;
            case EVENTS_TAB:
                EventsTab eventsTab = new EventsTab();
                generateEventsTab(eventsTab, muid);
                return eventsTab;
            case USERS_TAB:
                UsersTab usersTab = new UsersTab();
                generateUsersTab(usersTab, muid);
                return usersTab;
            case PHOTOS_TAB:
                PhotosTab photosTab = new PhotosTab();
                generatePhotosTab(photosTab, muid);
                return photosTab;
            case RECOMMENDATIONS_TAB:
                RecommendationsTab recommendationsTab = new RecommendationsTab();
                generateRecommendationsTab(recommendationsTab, muid);
                return recommendationsTab;

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabType.toString() + ".");
        }
    }

    private EntityTabPreview generateTabPreview(
            EntityTabType entityTabPreviewType, Muid muid) {
        switch (entityTabPreviewType) {
            case INFO_TAB:
                InfoTabPreview infoTabPreview = new InfoTabPreview();
                generateInfoTabPreview(infoTabPreview, muid);
                return infoTabPreview;
            case NEWSFEED_TAB:
                NewsfeedTabPreview newsfeedTabPreview = new NewsfeedTabPreview();
                generateNewsfeedTabPreview(newsfeedTabPreview, muid);
                return newsfeedTabPreview;
            case BANDS_TAB:
                BandsTabPreview bandsTabPreview = new BandsTabPreview();
                generateBandsTabPreview(bandsTabPreview, muid);
                return bandsTabPreview;
            case RECORDS_TAB:
                RecordsTabPreview recordsTabPreview = new RecordsTabPreview();
                generateRecordsTabPreview(recordsTabPreview, muid);
                return recordsTabPreview;
            case TRACKS_TAB:
                TracksTabPreview tracksTabPreview = new TracksTabPreview();
                generateTracksTabPreview(tracksTabPreview, muid);
                return tracksTabPreview;
            case REVIEWS_TAB:
                ReviewsTabPreview reviewsTabPreview = new ReviewsTabPreview();
                generateReviewsTabPreview(reviewsTabPreview, muid);
                return reviewsTabPreview;
            case VENUES_TAB:
                VenuesTabPreview venuesTabPreview = new VenuesTabPreview();
                generateVenuesTabPreview(venuesTabPreview, muid);
                return venuesTabPreview;
            case EVENTS_TAB:
                EventsTabPreview eventsTabPreview = new EventsTabPreview();
                generateEventsTabPreview(eventsTabPreview, muid);
                return eventsTabPreview;
            case USERS_TAB:
                UsersTabPreview usersTabPreview = new UsersTabPreview();
                generateUsersTabPreview(usersTabPreview, muid);
                return usersTabPreview;
            case PHOTOS_TAB:
                PhotosTabPreview photosTabPreview = new PhotosTabPreview();
                generatePhotosTabPreview(photosTabPreview, muid);
                return photosTabPreview;
            case RECOMMENDATIONS_TAB:
                RecommendationsTabPreview recommendationsTabPreview = new RecommendationsTabPreview();
                generateRecommendationsTabPreview(recommendationsTabPreview, muid);
                return recommendationsTabPreview;
                
            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabPreviewType.toString() + ".");
        }
    }
    
    
    // InfoTab
    
    protected void generateInfoTab(InfoTab tab, Muid muid) {
    }
      
    protected void generateInfoTabPreview(InfoTabPreview tab, Muid muid) {
    }
      
    // NewsfeedTab
    
    protected void generateNewsfeedTab(NewsfeedTab tab, Muid muid) {
    }
    
    protected void generateNewsfeedTabPreview(NewsfeedTabPreview tab, Muid muid) {
    }
    
    // BandsTab
    
    protected void generateBandsTab(BandsTab tab, Muid muid) {
    }
    
    protected void generateBandsTabPreview(BandsTabPreview tab, Muid muid) {
    }
    
    // RecordsTab
    
    protected void generateRecordsTab(RecordsTab tab, Muid muid) {
    }
    
    protected void generateRecordsTabPreview(RecordsTabPreview tab, Muid muid) {
    }
    
    // TracksTab
    
    protected void generateTracksTab(TracksTab tab, Muid muid) {
    }
    
    protected void generateTracksTabPreview(TracksTabPreview tab, Muid muid) {
    }
    
    // ReviewsTab
    
    protected void generateReviewsTab(ReviewsTab tab, Muid muid) {
    }
    
    protected void generateReviewsTabPreview(ReviewsTabPreview tab, Muid muid) {
    }
    
    // VenuesTab
    
    protected void generateVenuesTab(VenuesTab tab, Muid muid) {
    }
    
    protected void generateVenuesTabPreview(VenuesTabPreview tab, Muid muid) {
    }
    
    // EventsTab
    
    protected void generateEventsTab(EventsTab tab, Muid muid) {
    }
    
    protected void generateEventsTabPreview(EventsTabPreview tab, Muid muid) {
    }
    
    // UsersTab
    
    protected void generateUsersTab(UsersTab tab, Muid muid) {
    }
    
    protected void generateUsersTabPreview(UsersTabPreview tab, Muid muid) {
    }
    
    // PhotosTab
   
    protected void generatePhotosTab(PhotosTab tab, Muid muid) {
    }
    
    protected void generatePhotosTabPreview(PhotosTabPreview tab, Muid muid) {
    }
    
    // RecommendationsTab
    
    protected void generateRecommendationsTab(RecommendationsTab tab, Muid muid) {
    }
    
    protected void generateRecommendationsTabPreview(RecommendationsTabPreview tab, Muid muid) {
    }
    
    
    // RequestMappings
    
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