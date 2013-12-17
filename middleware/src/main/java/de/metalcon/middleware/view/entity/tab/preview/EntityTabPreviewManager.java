package de.metalcon.middleware.view.entity.tab.preview;

import de.metalcon.middleware.view.entity.tab.EntityTabType;
import de.metalcon.middleware.view.entity.tab.preview.impl.AboutTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.BandsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.EventsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.NewsfeedTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.PhotosTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.RecommendationsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.RecordsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.ReviewsTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.TracksTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.UsersTabPreview;
import de.metalcon.middleware.view.entity.tab.preview.impl.VenuesTabPreview;

public abstract class EntityTabPreviewManager {

    public EntityTabPreview createTabPreview(EntityTabType entityTabType) {
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

    public abstract AboutTabPreview createAboutTab();

    public abstract BandsTabPreview createBandsTab();

    public abstract EventsTabPreview createEventsTab();

    public abstract NewsfeedTabPreview createNewsfeedTab();

    public abstract PhotosTabPreview createPhotosTab();

    public abstract RecommendationsTabPreview createRecomendationsTab();

    public abstract RecordsTabPreview createRecordsTab();

    public abstract ReviewsTabPreview createReviewsTab();

    public abstract TracksTabPreview createTracksTab();

    public abstract UsersTabPreview createUsersTab();

    public abstract VenuesTabPreview createVenuesTab();

}
