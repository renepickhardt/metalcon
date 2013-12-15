package de.metalcon.middleware.view.entity.tab.preview;

import de.metalcon.middleware.view.entity.tab.EntityTabType;

public abstract class EntityTabPreviewManager {

    public EntityTabPreview createTabPreview(EntityTabType entityTabType) {
        switch (entityTabType) {
            case ABOUT_TAB:           return createAboutTabPreview();
            case NEWSFEED_TAB:        return createNewsfeedTabPreview();
            case BANDS_TAB:           return createBandsTabPreview();
            case RECORDS_TAB:         return createRecordsTabPreview();
            case TRACKS_TAB:          return createTracksTabPreview();
            case REVIEWS_TAB:         return createReviewsTabPreview();
            case VENUES_TAB:          return createVenuesTabPreview();
            case EVENTS_TAB:          return createEventsTabPreview();
            case USERS_TAB:           return createUsersTabPreview();
            case PHOTOS_TAB:          return createPhotosTabPreview();
            case RECOMMENDATIONS_TAB: return createRecomendationsTabPreview();
            
            default:
                throw new IllegalStateException("Unimplemented EntityTabPreviewType:"
                        + entityTabType.toString() + ".");
        }
    }
    
    public abstract AboutTabPreview createAboutTabPreview();
    
    public abstract NewsfeedTabPreview createNewsfeedTabPreview();
    
    public abstract BandsTabPreview createBandsTabPreview();
    
    public abstract RecordsTabPreview createRecordsTabPreview();
    
    public abstract TracksTabPreview createTracksTabPreview();
    
    public abstract ReviewsTabPreview createReviewsTabPreview();
    
    public abstract VenuesTabPreview createVenuesTabPreview();
   
    public abstract EventsTabPreview createEventsTabPreview();
    
    public abstract UsersTabPreview createUsersTabPreview();
    
    public abstract PhotosTabPreview createPhotosTabPreview();
    
    public abstract RecommendationsTabPreview createRecomendationsTabPreview();
    
}
