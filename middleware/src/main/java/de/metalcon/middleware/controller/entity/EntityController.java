package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.controller.UrlMappings;
import de.metalcon.middleware.controller.entity.generating.AboutTabGenerating;
import de.metalcon.middleware.controller.entity.generating.BandsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.EventsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.NewsfeedTabGenerating;
import de.metalcon.middleware.controller.entity.generating.PhotosTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecommendationsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.RecordsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.ReviewsTabGenerating;
import de.metalcon.middleware.controller.entity.generating.TracksTabGenerating;
import de.metalcon.middleware.controller.entity.generating.UsersTabGenerating;
import de.metalcon.middleware.controller.entity.generating.VenuesTabGenerating;
import de.metalcon.middleware.controller.entity.generator.EntityTabGenerator;
import de.metalcon.middleware.core.EntityManager;
import de.metalcon.middleware.core.EntityUrlMapppingManager;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;
import de.metalcon.middleware.view.entity.EntityView;
import de.metalcon.middleware.view.entity.EntityViewFactory;
import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContent;
import de.metalcon.middleware.view.entity.tab.content.EntityTabContentFactory;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.EntityTabPreviewFactory;

public abstract class EntityController extends MetalconController {

    @Autowired
    private EntityViewFactory entityViewFactory;

    @Autowired
    private EntityTabContentFactory entityTabContentFactory;

    @Autowired
    private EntityTabPreviewFactory entityTabPreviewFactory;

    @Autowired
    protected EntityUrlMapppingManager entityUrlMappingManager;

    @Autowired
    private EntityManager entityManager;

    private Map<EntityTabType, EntityTabGenerator> entityTabsGenerators;

    public EntityController() {
        entityTabsGenerators = new HashMap<EntityTabType, EntityTabGenerator>();
    }

    @PostConstruct
    public void init() {
        fillEntityTabGenerators();
    }

    private void fillEntityTabGenerators() {
        // @formatter:off
        if (this instanceof AboutTabGenerating)           entityTabsGenerators.put(EntityTabType.ABOUT_TAB,           ((AboutTabGenerating)           this).getAboutTabGenerator());
        if (this instanceof BandsTabGenerating)           entityTabsGenerators.put(EntityTabType.BANDS_TAB,           ((BandsTabGenerating)           this).getBandsTabGenerator());
        if (this instanceof EventsTabGenerating)          entityTabsGenerators.put(EntityTabType.EVENTS_TAB,          ((EventsTabGenerating)          this).getEventsTabGenerator());
        if (this instanceof NewsfeedTabGenerating)        entityTabsGenerators.put(EntityTabType.NEWSFEED_TAB,        ((NewsfeedTabGenerating)        this).getNewsfeedTabGenerator());
        if (this instanceof PhotosTabGenerating)          entityTabsGenerators.put(EntityTabType.PHOTOS_TAB,          ((PhotosTabGenerating)          this).getPhotosTabGenerator());
        if (this instanceof RecommendationsTabGenerating) entityTabsGenerators.put(EntityTabType.RECOMMENDATIONS_TAB, ((RecommendationsTabGenerating) this).getRecommendationsTabGenerator());
        if (this instanceof RecordsTabGenerating)         entityTabsGenerators.put(EntityTabType.RECORDS_TAB,         ((RecordsTabGenerating)         this).getRecordsTabGenerator());
        if (this instanceof ReviewsTabGenerating)         entityTabsGenerators.put(EntityTabType.REVIEWS_TAB,         ((ReviewsTabGenerating)         this).getReviewsTabGenerator());
        if (this instanceof TracksTabGenerating)          entityTabsGenerators.put(EntityTabType.TRACKS_TAB,          ((TracksTabGenerating)          this).getTracksTabGenerator());
        if (this instanceof UsersTabGenerating)           entityTabsGenerators.put(EntityTabType.USERS_TAB,           ((UsersTabGenerating)           this).getUsersTabGenerator());
        if (this instanceof VenuesTabGenerating)          entityTabsGenerators.put(EntityTabType.VENUES_TAB,          ((VenuesTabGenerating)          this).getVenuesTabGenerator());
        // @formatter:on
    }

    public abstract EntityType getEntityType();

    protected EntityTabType getDefaultTab() {
        return EntityTabType.NEWSFEED_TAB;
    }

    private final EntityView handleTab(
            EntityTabType entityTabType,
            HttpServletRequest request,
            Map<String, String> pathVars) throws RedirectException,
            NoSuchRequestHandlingMethodException {
        if (entityTabType == EntityTabType.EMPTY_TAB)
            entityTabType = getDefaultTab();

        Muid muid = entityUrlMappingManager.getMuid(getEntityType(), pathVars);

        if (!entityTabsGenerators.containsKey(entityTabType) || muid == null)
            throw new NoSuchRequestHandlingMethodException(request);

        Entity entity = entityManager.getEntity(muid);

        Map<EntityTabType, EntityTabPreview> entityTabPreviews =
                new HashMap<EntityTabType, EntityTabPreview>();
        for (Map.Entry<EntityTabType, EntityTabGenerator> entry : entityTabsGenerators
                .entrySet()) {
            EntityTabType entityTabPreviewType = entry.getKey();
            EntityTabGenerator entityTabPreviewGenerator = entry.getValue();

            EntityTabPreview entityTabPreview =
                    entityTabPreviewFactory
                            .createTabPreview(entityTabPreviewType);
            entityTabPreviewGenerator.genereteTabPreview(entityTabPreview,
                    entity);
            entityTabPreviews.put(entityTabPreviewType, entityTabPreview);
        }

        EntityTabContent entityTabContent =
                entityTabContentFactory.createTabContent(entityTabType);
        entityTabsGenerators.get(entityTabType).generateTabContent(
                entityTabContent, entity);

        EntityView entityView = entityViewFactory.createView(getEntityType());
        entityView.setMuid(muid);
        entityView.setEntityTabContent(entityTabContent);
        entityView.setEntityTabPreviews(entityTabPreviews);

        return entityView;
    }

    @RequestMapping({
        "", "/"
    })
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

    @RequestMapping(UrlMappings.BANDS_TAB_MAPPING)
    public final EntityView mappingBandsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.BANDS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.EVENTS_TAB_MAPPING)
    public final EntityView mappingEventsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.EVENTS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.NEWSFEED_TAB_MAPPING)
    public final EntityView mappingNewsfeedTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.NEWSFEED_TAB, request, pathVars);
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

    @RequestMapping(UrlMappings.RECORDS_TAB_MAPPING)
    public final EntityView mappingRecordsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.RECORDS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.REVIEWS_TAB_MAPPING)
    public final EntityView mappingReviewsTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.REVIEWS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.TRACKS_TAB_MAPPING)
    public final EntityView mappingTracksTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.TRACKS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.USERS_TAB_MAPPING)
    public final EntityView mappingUsersTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.USERS_TAB, request, pathVars);
    }

    @RequestMapping(UrlMappings.VENUES_TAB_MAPPING)
    public final EntityView mappingVenuesTab(
            HttpServletRequest request,
            @PathVariable Map<String, String> pathVars)
            throws RedirectException, NoSuchRequestHandlingMethodException {
        return handleTab(EntityTabType.VENUES_TAB, request, pathVars);
    }

}
