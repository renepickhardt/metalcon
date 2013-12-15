package de.metalcon.middleware.view.entity.tab;

public abstract class EntityTabManager {

    public EntityTab createTab(EntityTabType entityTabType) {
        switch (entityTabType) {
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
            
            default:
                throw new IllegalStateException("Unimplemented EntityTabType:"
                        + entityTabType.toString() + ".");
        }
    }
    
    public abstract AboutTab createAboutTab();
    
    public abstract NewsfeedTab createNewsfeedTab();
    
    public abstract BandsTab createBandsTab();
    
    public abstract RecordsTab createRecordsTab();
    
    public abstract TracksTab createTracksTab();
    
    public abstract ReviewsTab createReviewsTab();
    
    public abstract VenuesTab createVenuesTab();
   
    public abstract EventsTab createEventsTab();
    
    public abstract UsersTab createUsersTab();
    
    public abstract PhotosTab createPhotosTab();
    
    public abstract RecommendationsTab createRecomendationsTab();
    
}
