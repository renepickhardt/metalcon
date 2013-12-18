package de.metalcon.middleware.view.entity.tab.preview;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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

@Configuration
public class EntityTabPreviewConfig {

    @Bean
    @Scope("prototype")
    public AboutTabPreview aboutTabPreview() {
        return new AboutTabPreview();
    }

    @Bean
    @Scope("prototype")
    public BandsTabPreview bandsTabPreview() {
        return new BandsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public EventsTabPreview eventsTabPreview() {
        return new EventsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public NewsfeedTabPreview newsfeedTabPreview() {
        return new NewsfeedTabPreview();
    }

    @Bean
    @Scope("prototype")
    public PhotosTabPreview photosTabPreview() {
        return new PhotosTabPreview();
    }

    @Bean
    @Scope("prototype")
    public RecommendationsTabPreview recommendationsTabPreview() {
        return new RecommendationsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public RecordsTabPreview recordsTabPreview() {
        return new RecordsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public ReviewsTabPreview reviewsTabPreview() {
        return new ReviewsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public TracksTabPreview tracksTabPreview() {
        return new TracksTabPreview();
    }

    @Bean
    @Scope("prototype")
    public UsersTabPreview usersTabPreview() {
        return new UsersTabPreview();
    }

    @Bean
    @Scope("prototype")
    public VenuesTabPreview venuesTabPreview() {
        return new VenuesTabPreview();
    }

    @Bean
    public EntityTabPreviewFactory entityTabPreviewFactory() {
        return new EntityTabPreviewFactory() {

            @Override
            public AboutTabPreview createAboutTabPreview() {
                return aboutTabPreview();
            }

            @Override
            public BandsTabPreview createBandsTabPreview() {
                return bandsTabPreview();
            }

            @Override
            public EventsTabPreview createEventsTabPreview() {
                return eventsTabPreview();
            }

            @Override
            public NewsfeedTabPreview createNewsfeedTabPreview() {
                return newsfeedTabPreview();
            }

            @Override
            public PhotosTabPreview createPhotosTabPreview() {
                return photosTabPreview();
            }

            @Override
            public RecommendationsTabPreview createRecomendationsTabPreview() {
                return recommendationsTabPreview();
            }

            @Override
            public RecordsTabPreview createRecordsTabPreview() {
                return recordsTabPreview();
            }

            @Override
            public ReviewsTabPreview createReviewsTabPreview() {
                return reviewsTabPreview();
            }

            @Override
            public TracksTabPreview createTracksTabPreview() {
                return tracksTabPreview();
            }

            @Override
            public UsersTabPreview createUsersTabPreview() {
                return usersTabPreview();
            }

            @Override
            public VenuesTabPreview createVenuesTabPreview() {
                return venuesTabPreview();
            }

        };
    }

}
