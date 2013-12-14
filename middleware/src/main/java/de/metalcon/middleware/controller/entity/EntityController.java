package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;
import de.metalcon.middleware.view.entity.BandView;
import de.metalcon.middleware.view.entity.EntityView;
import de.metalcon.middleware.view.entity.RecordView;
import de.metalcon.middleware.view.entity.TrackView;
import de.metalcon.middleware.view.entity.tab.BandsTab;
import de.metalcon.middleware.view.entity.tab.EntityTab;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.EventsTab;
import de.metalcon.middleware.view.entity.tab.AboutTab;
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
import de.metalcon.middleware.view.entity.tab.preview.AboutTabPreview;
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
    
    @Autowired
    private BeanFactory beanFactory;
    
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
    
    private final EntityView handleTab(
            EntityTabType entityTabType, HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();
        
        @SuppressWarnings("unchecked")
        Map<String, String> pathVars =
                (Map<String, String>) request.getAttribute(View.PATH_VARIABLES);
        Muid muid = urlMapper.getMuid(getEntityType(), pathVars);
        
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
        
        EntityView entityView = createEntityView(getEntityType());
        entityView.setMuid(muid);
        entityView.setEntityTab(entityTab);
        entityView.setEntityTabPreviews(entityTabPreviews);

        return entityView;
    }
    
    private EntityView createEntityView(EntityType entityType) {
        switch(entityType) {
            case BAND:   return beanFactory.getBean(BandView.class);
            case RECORD: return beanFactory.getBean(RecordView.class);
            case TRACK:  return beanFactory.getBean(TrackView.class);
                
            default:
                throw new IllegalStateException(
                        "Unimplemented EntityType in EntityController.createEntityView(): "
                                + entityType.toString() + ".");
        }
    }
    
    private EntityTab generateTab(
            EntityTabType entityTabType, Muid muid) {
        switch (entityTabType) {
            case ABOUT_TAB:
                AboutTab aboutTab = new AboutTab();
                generateAboutTab(aboutTab, muid);
                return aboutTab;
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
            case ABOUT_TAB:
                AboutTabPreview aboutTabPreview = new AboutTabPreview();
                generateAboutTabPreview(aboutTabPreview, muid);
                return aboutTabPreview;
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
    
    
    // AboutTab
    
    protected void generateAboutTab(AboutTab tab, Muid muid) {
    }
      
    protected void generateAboutTabPreview(AboutTabPreview tab, Muid muid) {
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
    
    @RequestMapping({"", "/"})
    public final EntityView mappingEmptyTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EMPTY_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.ABOUT_TAB_MAPPING)
    public final EntityView mappingAboutTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.ABOUT_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.NEWSFEED_TAB_MAPPING)
    public final EntityView mappingNewsfeedTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.NEWSFEED_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.BANDS_TAB_MAPPING)
    public final EntityView mappingBandsTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.BANDS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.RECORDS_TAB_MAPPING)
    public final EntityView mappingRecordsTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECORDS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.TRACKS_TAB_MAPPING)
    public final EntityView mappingTracksTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.TRACKS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.REVIEWS_TAB_MAPPING)
    public final EntityView mappingReviewsTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.REVIEWS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.VENUES_TAB_MAPPING)
    public final EntityView mappingVenuesTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.VENUES_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.EVENTS_TAB_MAPPING)
    public final EntityView mappingEventsTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EVENTS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.USERS_TAB_MAPPING)
    public final EntityView mappingUsersTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.USERS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.PHOTOS_TAB_MAPPING)
    public final EntityView mappingPhotosTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.PHOTOS_TAB, request);
    }
   
    @RequestMapping(EntityUrlMapper.RECOMMENDATIONS_TAB_MAPPING)
    public final EntityView mappingRecommendationsTab(HttpServletRequest request)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECOMMENDATIONS_TAB, request);
    }
   
}