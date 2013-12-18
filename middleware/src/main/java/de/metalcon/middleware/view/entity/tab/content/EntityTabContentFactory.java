package de.metalcon.middleware.view.entity.tab.content;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.content.impl.AboutTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.BandsTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.EventsTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.NewsfeedTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.PhotosTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.RecommendationsTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.RecordsTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.ReviewsTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.TracksTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.UsersTabContent;
import de.metalcon.middleware.view.entity.tab.content.impl.VenuesTabContent;

public abstract class EntityTabContentFactory {

    public EntityTabContent createTabContent(EntityTabType entityTabType) {
        switch (entityTabType) {
        // @formatter:off
            case ABOUT_TAB:           return createAboutTabContent();
            case BANDS_TAB:           return createBandsTabContent();
            case EVENTS_TAB:          return createEventsTabContent();
            case NEWSFEED_TAB:        return createNewsfeedTabContent();
            case PHOTOS_TAB:          return createPhotosTabContent();
            case RECOMMENDATIONS_TAB: return createRecomendationsTabContent();
            case RECORDS_TAB:         return createRecordsTabContent();
            case REVIEWS_TAB:         return createReviewsTabContent();
            case TRACKS_TAB:          return createTracksTabContent();
            case USERS_TAB:           return createUsersTabContent();
            case VENUES_TAB:          return createVenuesTabContent();
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplemented EntityTabType:"
                        + entityTabType.toString() + ".");
        }
    }

    public abstract AboutTabContent createAboutTabContent();

    public abstract BandsTabContent createBandsTabContent();

    public abstract EventsTabContent createEventsTabContent();

    public abstract NewsfeedTabContent createNewsfeedTabContent();

    public abstract PhotosTabContent createPhotosTabContent();

    public abstract RecommendationsTabContent createRecomendationsTabContent();

    public abstract RecordsTabContent createRecordsTabContent();

    public abstract ReviewsTabContent createReviewsTabContent();

    public abstract TracksTabContent createTracksTabContent();

    public abstract UsersTabContent createUsersTabContent();

    public abstract VenuesTabContent createVenuesTabContent();

}
