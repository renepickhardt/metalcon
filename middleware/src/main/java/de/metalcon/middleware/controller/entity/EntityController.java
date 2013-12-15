package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.core.EntityUrlMapper;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.EntityView;
import de.metalcon.middleware.view.entity.EntityViewManager;
import de.metalcon.middleware.view.entity.tab.BandsTab;
import de.metalcon.middleware.view.entity.tab.EntityTab;
import de.metalcon.middleware.view.entity.tab.EntityTabManager;
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
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreviewManager;
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
    private EntityViewManager entityViewManager;
    
    @Autowired
    private EntityTabManager entityTabManager;
    
    @Autowired
    private EntityTabPreviewManager entityTabPreviewManager;
    
    private Set<EntityTabType> entityTabs;
    
    public abstract EntityType getEntityType();
    
    public EntityController() {
        entityTabs = new HashSet<EntityTabType>();
    }
    
    protected EntityTabType getDefaultTab() {
        return EntityTabType.NEWSFEED_TAB;
    }
    
    protected final void registerTab(EntityTabType entityTabType) {
        entityTabs.add(entityTabType);
    }
    
    private final EntityView handleTab(
            EntityTabType entityTabType,
            HttpServletRequest request,
            Map<String, String> pathVars)
    throws Exception {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();
        
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
        
        EntityView entityView = entityViewManager.createView(getEntityType());
        entityView.setMuid(muid);
        entityView.setEntityTab(entityTab);
        entityView.setEntityTabPreviews(entityTabPreviews);

        return entityView;
    }
    
    
    // generate<Tab>
    
    private EntityTab generateTab(
            EntityTabType entityTabType, Muid muid) {
        EntityTab entityTab = entityTabManager.createTab(entityTabType);
        switch (entityTabType) {
            case ABOUT_TAB:           generateAboutTab          ((AboutTab)           entityTab, muid); break;
            case NEWSFEED_TAB:        generateNewsfeedTab       ((NewsfeedTab)        entityTab, muid); break;
            case BANDS_TAB:           generateBandsTab          ((BandsTab)           entityTab, muid); break;
            case RECORDS_TAB:         generateRecordsTab        ((RecordsTab)         entityTab, muid); break;
            case TRACKS_TAB:          generateTracksTab         ((TracksTab)          entityTab, muid); break;
            case REVIEWS_TAB:         generateReviewsTab        ((ReviewsTab)         entityTab, muid); break;
            case VENUES_TAB:          generateVenuesTab         ((VenuesTab)          entityTab, muid); break;
            case EVENTS_TAB:          generateEventsTab         ((EventsTab)          entityTab, muid); break;
            case USERS_TAB:           generateUsersTab          ((UsersTab)           entityTab, muid); break;
            case PHOTOS_TAB:          generatePhotosTab         ((PhotosTab)          entityTab, muid); break;
            case RECOMMENDATIONS_TAB: generateRecommendationsTab((RecommendationsTab) entityTab, muid); break;

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabType.toString() + ".");
        }
        return entityTab;
    }
    
    protected void generateAboutTab(AboutTab tab, Muid muid) {
    }
      
    protected void generateNewsfeedTab(NewsfeedTab tab, Muid muid) {
    }
    
    protected void generateBandsTab(BandsTab tab, Muid muid) {
    }
    
    protected void generateRecordsTab(RecordsTab tab, Muid muid) {
    }
    
    protected void generateTracksTab(TracksTab tab, Muid muid) {
    }
    
    protected void generateReviewsTab(ReviewsTab tab, Muid muid) {
    }
    
    protected void generateVenuesTab(VenuesTab tab, Muid muid) {
    }
    
    protected void generateEventsTab(EventsTab tab, Muid muid) {
    }
    
    protected void generateUsersTab(UsersTab tab, Muid muid) {
    }
    
    protected void generatePhotosTab(PhotosTab tab, Muid muid) {
    }
    
    
    // generate<Tab>Preview
    
    private EntityTabPreview generateTabPreview(
            EntityTabType entityTabPreviewType, Muid muid) {
        EntityTabPreview entityTabPreview = entityTabPreviewManager.createTabPreview(entityTabPreviewType);
        switch (entityTabPreviewType) {
            case ABOUT_TAB:           generateAboutTabPreview          ((AboutTabPreview)           entityTabPreview, muid); break;
            case NEWSFEED_TAB:        generateNewsfeedTabPreview       ((NewsfeedTabPreview)        entityTabPreview, muid); break;
            case BANDS_TAB:           generateBandsTabPreview          ((BandsTabPreview)           entityTabPreview, muid); break;
            case RECORDS_TAB:         generateRecordsTabPreview        ((RecordsTabPreview)         entityTabPreview, muid); break;
            case TRACKS_TAB:          generateTracksTabPreview         ((TracksTabPreview)          entityTabPreview, muid); break;
            case REVIEWS_TAB:         generateReviewsTabPreview        ((ReviewsTabPreview)         entityTabPreview, muid); break;
            case VENUES_TAB:          generateVenuesTabPreview         ((VenuesTabPreview)          entityTabPreview, muid); break;
            case EVENTS_TAB:          generateEventsTabPreview         ((EventsTabPreview)          entityTabPreview, muid); break;
            case USERS_TAB:           generateUsersTabPreview          ((UsersTabPreview)           entityTabPreview, muid); break;
            case PHOTOS_TAB:          generatePhotosTabPreview         ((PhotosTabPreview)          entityTabPreview, muid); break;
            case RECOMMENDATIONS_TAB: generateRecommendationsTabPreview((RecommendationsTabPreview) entityTabPreview, muid); break;

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTabPreview(): "
                                + entityTabPreviewType.toString() + ".");
        }
        return entityTabPreview;
    }
    
    protected void generateAboutTabPreview(AboutTabPreview tab, Muid muid) {
    }
      
    protected void generateNewsfeedTabPreview(NewsfeedTabPreview tab, Muid muid) {
    }
    
    protected void generateBandsTabPreview(BandsTabPreview tab, Muid muid) {
    }
    
    protected void generateRecordsTabPreview(RecordsTabPreview tab, Muid muid) {
    }
    
    protected void generateTracksTabPreview(TracksTabPreview tab, Muid muid) {
    }
    
    protected void generateReviewsTabPreview(ReviewsTabPreview tab, Muid muid) {
    }
    
    protected void generateVenuesTabPreview(VenuesTabPreview tab, Muid muid) {
    }
    
    protected void generateEventsTabPreview(EventsTabPreview tab, Muid muid) {
    }
    
    protected void generateUsersTabPreview(UsersTabPreview tab, Muid muid) {
    }
    
    protected void generatePhotosTabPreview(PhotosTabPreview tab, Muid muid) {
    }
    
    protected void generateRecommendationsTab(RecommendationsTab tab, Muid muid) {
    }
    
    protected void generateRecommendationsTabPreview(RecommendationsTabPreview tab, Muid muid) {
    }
    
    
    // mapping<Tab>
    
    @RequestMapping({"", "/"})
    public final EntityView mappingEmptyTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.EMPTY_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.ABOUT_TAB_MAPPING)
    public final EntityView mappingAboutTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.ABOUT_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.NEWSFEED_TAB_MAPPING)
    public final EntityView mappingNewsfeedTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.NEWSFEED_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.BANDS_TAB_MAPPING)
    public final EntityView mappingBandsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.BANDS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.RECORDS_TAB_MAPPING)
    public final EntityView mappingRecordsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.RECORDS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.TRACKS_TAB_MAPPING)
    public final EntityView mappingTracksTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.TRACKS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.REVIEWS_TAB_MAPPING)
    public final EntityView mappingReviewsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.REVIEWS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.VENUES_TAB_MAPPING)
    public final EntityView mappingVenuesTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.VENUES_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.EVENTS_TAB_MAPPING)
    public final EntityView mappingEventsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.EVENTS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.USERS_TAB_MAPPING)
    public final EntityView mappingUsersTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.USERS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.PHOTOS_TAB_MAPPING)
    public final EntityView mappingPhotosTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.PHOTOS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.RECOMMENDATIONS_TAB_MAPPING)
    public final EntityView mappingRecommendationsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws Exception {
        return handleTab(EntityTabType.RECOMMENDATIONS_TAB, request, pathVars);
    }
   
}