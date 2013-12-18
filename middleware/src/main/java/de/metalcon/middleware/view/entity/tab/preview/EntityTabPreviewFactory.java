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

public abstract class EntityTabPreviewFactory {

    public EntityTabPreview createTabPreview(EntityTabType entityTabType) {
        switch (entityTabType) {
        // @formatter:off
            case ABOUT_TAB:           return createAboutTabPreview();
            case BANDS_TAB:           return createBandsTabPreview();
            case EVENTS_TAB:          return createEventsTabPreview();
            case NEWSFEED_TAB:        return createNewsfeedTabPreview();
            case PHOTOS_TAB:          return createPhotosTabPreview();
            case RECOMMENDATIONS_TAB: return createRecomendationsTabPreview();
            case RECORDS_TAB:         return createRecordsTabPreview();
            case REVIEWS_TAB:         return createReviewsTabPreview();
            case TRACKS_TAB:          return createTracksTabPreview();
            case USERS_TAB:           return createUsersTabPreview();
            case VENUES_TAB:          return createVenuesTabPreview();
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplemented EntityTabType:"
                        + entityTabType.toString() + ".");
        }
    }

    public abstract AboutTabPreview createAboutTabPreview();

    public abstract BandsTabPreview createBandsTabPreview();

    public abstract EventsTabPreview createEventsTabPreview();

    public abstract NewsfeedTabPreview createNewsfeedTabPreview();

    public abstract PhotosTabPreview createPhotosTabPreview();

    public abstract RecommendationsTabPreview createRecomendationsTabPreview();

    public abstract RecordsTabPreview createRecordsTabPreview();

    public abstract ReviewsTabPreview createReviewsTabPreview();

    public abstract TracksTabPreview createTracksTabPreview();

    public abstract UsersTabPreview createUsersTabPreview();

    public abstract VenuesTabPreview createVenuesTabPreview();

}
