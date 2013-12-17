package de.metalcon.middleware.view.entity.tab.content;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public abstract class EntityTabContentManager {

    public EntityTabContent createTab(EntityTabType entityTabType) {
        switch (entityTabType) {
        // @formatter:off
            case ABOUT_TAB:           return createAboutTab();
            case NEWSFEED_TAB:        return createNewsfeedTab();
            case BANDS_TAB:           return createBandsTab();
            case RECORDS_TAB:         return createRecordsTab();
            case TRACKS_TAB:          return createTracksTab();
            case REVIEWS_TAB:         return createReviewsTab();
            case VENUES_TAB:          return createVenuesTab();
            case EVENTS_TAB:          return createEventsTab();
            case USERS_TAB:           return createUsersTab();
            case PHOTOS_TAB:          return createPhotosTab();
            case RECOMMENDATIONS_TAB: return createRecomendationsTab();
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplemented EntityTabType:"
                        + entityTabType.toString() + ".");
        }
    }

    public abstract AboutContentTab createAboutTab();

    public abstract NewsfeedContentTab createNewsfeedTab();

    public abstract BandsContentTab createBandsTab();

    public abstract RecordsContentTab createRecordsTab();

    public abstract TracksContentTab createTracksTab();

    public abstract ReviewsContentTab createReviewsTab();

    public abstract VenuesContentTab createVenuesTab();

    public abstract EventsContentTab createEventsTab();

    public abstract UsersContentTab createUsersTab();

    public abstract PhotosContentTab createPhotosTab();

    public abstract RecommendationsContentTab createRecomendationsTab();

}
