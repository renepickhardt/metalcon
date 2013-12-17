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
    public AboutTabPreview aboutTab() {
        return new AboutTabPreview();
    }

    @Bean
    @Scope("prototype")
    public BandsTabPreview bandsTab() {
        return new BandsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public EventsTabPreview eventsTab() {
        return new EventsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public NewsfeedTabPreview newsfeedTab() {
        return new NewsfeedTabPreview();
    }

    @Bean
    @Scope("prototype")
    public PhotosTabPreview photosTab() {
        return new PhotosTabPreview();
    }

    @Bean
    @Scope("prototype")
    public RecommendationsTabPreview recommendationsTab() {
        return new RecommendationsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public RecordsTabPreview recordsTab() {
        return new RecordsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public ReviewsTabPreview reviewsTab() {
        return new ReviewsTabPreview();
    }

    @Bean
    @Scope("prototype")
    public TracksTabPreview tracksTab() {
        return new TracksTabPreview();
    }

    @Bean
    @Scope("prototype")
    public UsersTabPreview usersTab() {
        return new UsersTabPreview();
    }

    @Bean
    @Scope("prototype")
    public VenuesTabPreview venuesTab() {
        return new VenuesTabPreview();
    }

    @Bean
    public EntityTabPreviewManager entityTabManager() {
        return new EntityTabPreviewManager() {

            @Override
            public AboutTabPreview createAboutTab() {
                return aboutTab();
            }

            @Override
            public BandsTabPreview createBandsTab() {
                return bandsTab();
            }

            @Override
            public EventsTabPreview createEventsTab() {
                return eventsTab();
            }

            @Override
            public NewsfeedTabPreview createNewsfeedTab() {
                return newsfeedTab();
            }

            @Override
            public PhotosTabPreview createPhotosTab() {
                return photosTab();
            }

            @Override
            public RecommendationsTabPreview createRecomendationsTab() {
                return recommendationsTab();
            }

            @Override
            public RecordsTabPreview createRecordsTab() {
                return recordsTab();
            }

            @Override
            public ReviewsTabPreview createReviewsTab() {
                return reviewsTab();
            }

            @Override
            public TracksTabPreview createTracksTab() {
                return tracksTab();
            }

            @Override
            public UsersTabPreview createUsersTab() {
                return usersTab();
            }

            @Override
            public VenuesTabPreview createVenuesTab() {
                return venuesTab();
            }

        };
    }

}
