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
import de.metalcon.middleware.core.EntityUrlMapppingManager;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;
import de.metalcon.middleware.view.entity.EntityView;
import de.metalcon.middleware.view.entity.EntityViewManager;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.AboutContentTab;
import de.metalcon.middleware.view.entity.tab.content.BandsContentTab;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContentManager;
import de.metalcon.middleware.view.entity.tab.content.EventsContentTab;
import de.metalcon.middleware.view.entity.tab.content.NewsfeedContentTab;
import de.metalcon.middleware.view.entity.tab.content.PhotosContentTab;
import de.metalcon.middleware.view.entity.tab.content.RecommendationsContentTab;
import de.metalcon.middleware.view.entity.tab.content.RecordsContentTab;
import de.metalcon.middleware.view.entity.tab.content.ReviewsContentTab;
import de.metalcon.middleware.view.entity.tab.content.TracksContentTab;
import de.metalcon.middleware.view.entity.tab.content.UsersContentTab;
import de.metalcon.middleware.view.entity.tab.content.VenuesContentTab;
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
    protected EntityUrlMapppingManager entityUrlMappingManager;
    
    @Autowired
    private EntityViewManager entityViewManager;
    
    @Autowired
    private EntityTabContentManager entityTabContentManager;
    
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
    throws RedirectException, NoSuchRequestHandlingMethodException {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();
        
        Muid muid = entityUrlMappingManager.getMuid(getEntityType(), pathVars);
        
        if (!entityTabs.contains(entityTabType) || muid == null)
            throw new NoSuchRequestHandlingMethodException(request);
        
        Map<EntityTabType, EntityTabPreview> entityTabPreviews =
                new HashMap<EntityTabType, EntityTabPreview>();
        for (EntityTabType entityTabPreviewType : entityTabs) {
            EntityTabPreview entityTabPreview =
                    generateTabPreview(entityTabPreviewType, muid);
            entityTabPreviews.put(entityTabPreviewType, entityTabPreview);
        }
        
        EntityTabContent entityTabContent =
                generateTabContent(entityTabType, muid);
        
        EntityView entityView = entityViewManager.createView(getEntityType());
        entityView.setMuid(muid);
        entityView.setEntityTabContent(entityTabContent);
        entityView.setEntityTabPreviews(entityTabPreviews);

        return entityView;
    }
    
    
    // generate<Tab>Content
    
    private EntityTabContent generateTabContent(
            EntityTabType entityTabType, Muid muid) {
        EntityTabContent entityTab = entityTabContentManager.createTab(entityTabType);
        switch (entityTabType) {
            case ABOUT_TAB:           generateAboutTabContent          ((AboutContentTab)           entityTab, muid); break;
            case NEWSFEED_TAB:        generateNewsfeedTabContent       ((NewsfeedContentTab)        entityTab, muid); break;
            case BANDS_TAB:           generateBandsTabContent          ((BandsContentTab)           entityTab, muid); break;
            case RECORDS_TAB:         generateRecordsTabContent        ((RecordsContentTab)         entityTab, muid); break;
            case TRACKS_TAB:          generateTracksTabContent         ((TracksContentTab)          entityTab, muid); break;
            case REVIEWS_TAB:         generateReviewsTabContent        ((ReviewsContentTab)         entityTab, muid); break;
            case VENUES_TAB:          generateVenuesTabContent         ((VenuesContentTab)          entityTab, muid); break;
            case EVENTS_TAB:          generateEventsTabContent         ((EventsContentTab)          entityTab, muid); break;
            case USERS_TAB:           generateUsersTabContent          ((UsersContentTab)           entityTab, muid); break;
            case PHOTOS_TAB:          generatePhotosTabContent         ((PhotosContentTab)          entityTab, muid); break;
            case RECOMMENDATIONS_TAB: generateRecommendationsTabContent((RecommendationsContentTab) entityTab, muid); break;

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityTabType in EntityController.handleTab(): "
                                + entityTabType.toString() + ".");
        }
        return entityTab;
    }
    
    protected void generateAboutTabContent(AboutContentTab tab, Muid muid) {
    }
      
    protected void generateNewsfeedTabContent(NewsfeedContentTab tab, Muid muid) {
    }
    
    protected void generateBandsTabContent(BandsContentTab tab, Muid muid) {
    }
    
    protected void generateRecordsTabContent(RecordsContentTab tab, Muid muid) {
    }
    
    protected void generateTracksTabContent(TracksContentTab tab, Muid muid) {
    }
    
    protected void generateReviewsTabContent(ReviewsContentTab tab, Muid muid) {
    }
    
    protected void generateVenuesTabContent(VenuesContentTab tab, Muid muid) {
    }
    
    protected void generateEventsTabContent(EventsContentTab tab, Muid muid) {
    }
    
    protected void generateUsersTabContent(UsersContentTab tab, Muid muid) {
    }
    
    protected void generatePhotosTabContent(PhotosContentTab tab, Muid muid) {
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
    
    protected void generateRecommendationsTabContent(RecommendationsContentTab tab, Muid muid) {
    }
    
    protected void generateRecommendationsTabPreview(RecommendationsTabPreview tab, Muid muid) {
    }
    
    
    // mapping<Tab>
    
    @RequestMapping({"", "/"})
    public final EntityView mappingEmptyTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EMPTY_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.ABOUT_TAB_MAPPING)
    public final EntityView mappingAboutTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.ABOUT_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.NEWSFEED_TAB_MAPPING)
    public final EntityView mappingNewsfeedTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.NEWSFEED_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.BANDS_TAB_MAPPING)
    public final EntityView mappingBandsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.BANDS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.RECORDS_TAB_MAPPING)
    public final EntityView mappingRecordsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECORDS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.TRACKS_TAB_MAPPING)
    public final EntityView mappingTracksTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.TRACKS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.REVIEWS_TAB_MAPPING)
    public final EntityView mappingReviewsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.REVIEWS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.VENUES_TAB_MAPPING)
    public final EntityView mappingVenuesTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.VENUES_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.EVENTS_TAB_MAPPING)
    public final EntityView mappingEventsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EVENTS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.USERS_TAB_MAPPING)
    public final EntityView mappingUsersTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.USERS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.PHOTOS_TAB_MAPPING)
    public final EntityView mappingPhotosTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.PHOTOS_TAB, request, pathVars);
    }
   
    @RequestMapping(UrlMappings.RECOMMENDATIONS_TAB_MAPPING)
    public final EntityView mappingRecommendationsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
    throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECOMMENDATIONS_TAB, request, pathVars);
    }
   
}