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

public abstract class EntityTabContentManager {

    public EntityTabContent createTabContent(EntityTabType entityTabType) {
        switch (entityTabType) {
        // @formatter:off
            case ABOUT_TAB:           return createAboutTab();
            case BANDS_TAB:           return createBandsTab();
            case EVENTS_TAB:          return createEventsTab();
            case NEWSFEED_TAB:        return createNewsfeedTab();
            case PHOTOS_TAB:          return createPhotosTab();
            case RECOMMENDATIONS_TAB: return createRecomendationsTab();
            case RECORDS_TAB:         return createRecordsTab();
            case REVIEWS_TAB:         return createReviewsTab();
            case TRACKS_TAB:          return createTracksTab();
            case USERS_TAB:           return createUsersTab();
            case VENUES_TAB:          return createVenuesTab();
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplemented EntityTabType:"
                        + entityTabType.toString() + ".");
        }
    }

    public abstract AboutTabContent createAboutTab();

    public abstract BandsTabContent createBandsTab();

    public abstract EventsTabContent createEventsTab();

    public abstract NewsfeedTabContent createNewsfeedTab();

    public abstract PhotosTabContent createPhotosTab();

    public abstract RecommendationsTabContent createRecomendationsTab();

    public abstract RecordsTabContent createRecordsTab();

    public abstract ReviewsTabContent createReviewsTab();

    public abstract TracksTabContent createTracksTab();

    public abstract UsersTabContent createUsersTab();

    public abstract VenuesTabContent createVenuesTab();

}
