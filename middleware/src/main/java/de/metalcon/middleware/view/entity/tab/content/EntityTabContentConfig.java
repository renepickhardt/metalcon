package de.metalcon.middleware.view.entity.tab.content;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EntityTabContentConfig {

    @Bean
    @Scope("prototype")
    public AboutContentTab aboutTab() {
        return new AboutContentTab();
    }

    @Bean
    @Scope("prototype")
    public NewsfeedContentTab newsfeedTab() {
        return new NewsfeedContentTab();
    }

    @Bean
    @Scope("prototype")
    public BandsContentTab bandsTab() {
        return new BandsContentTab();
    }

    @Bean
    @Scope("prototype")
    public RecordsContentTab recordsTab() {
        return new RecordsContentTab();
    }

    @Bean
    @Scope("prototype")
    public TracksContentTab tracksTab() {
        return new TracksContentTab();
    }

    @Bean
    @Scope("prototype")
    public ReviewsContentTab reviewsTab() {
        return new ReviewsContentTab();
    }

    @Bean
    @Scope("prototype")
    public VenuesContentTab venuesTab() {
        return new VenuesContentTab();
    }

    @Bean
    @Scope("prototype")
    public EventsContentTab eventsTab() {
        return new EventsContentTab();
    }

    @Bean
    @Scope("prototype")
    public UsersContentTab usersTab() {
        return new UsersContentTab();
    }

    @Bean
    @Scope("prototype")
    public PhotosContentTab photosTab() {
        return new PhotosContentTab();
    }

    @Bean
    @Scope("prototype")
    public RecommendationsContentTab recommendationsTab() {
        return new RecommendationsContentTab();
    }

    @Bean
    public EntityTabContentManager entityTabManager() {
        return new EntityTabContentManager() {

            @Override
            public AboutContentTab createAboutTab() {
                return aboutTab();
            }

            @Override
            public NewsfeedContentTab createNewsfeedTab() {
                return newsfeedTab();
            }

            @Override
            public BandsContentTab createBandsTab() {
                return bandsTab();
            }

            @Override
            public RecordsContentTab createRecordsTab() {
                return recordsTab();
            }

            @Override
            public TracksContentTab createTracksTab() {
                return tracksTab();
            }

            @Override
            public ReviewsContentTab createReviewsTab() {
                return reviewsTab();
            }

            @Override
            public VenuesContentTab createVenuesTab() {
                return venuesTab();
            }

            @Override
            public EventsContentTab createEventsTab() {
                return eventsTab();
            }

            @Override
            public UsersContentTab createUsersTab() {
                return usersTab();
            }

            @Override
            public PhotosContentTab createPhotosTab() {
                return photosTab();
            }

            @Override
            public RecommendationsContentTab createRecomendationsTab() {
                return recommendationsTab();
            }

        };
    }

}
