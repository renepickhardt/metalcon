package de.metalcon.middleware.view.entity.tab.content;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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

@Configuration
public class EntityTabContentConfig {

    @Bean
    @Scope("prototype")
    public AboutTabContent aboutTabContent() {
        return new AboutTabContent();
    }

    @Bean
    @Scope("prototype")
    public BandsTabContent bandsTabContent() {
        return new BandsTabContent();
    }

    @Bean
    @Scope("prototype")
    public EventsTabContent eventsTabContent() {
        return new EventsTabContent();
    }

    @Bean
    @Scope("prototype")
    public NewsfeedTabContent newsfeedTabContent() {
        return new NewsfeedTabContent();
    }

    @Bean
    @Scope("prototype")
    public PhotosTabContent photosTabContent() {
        return new PhotosTabContent();
    }

    @Bean
    @Scope("prototype")
    public RecommendationsTabContent recommendationsTabContent() {
        return new RecommendationsTabContent();
    }

    @Bean
    @Scope("prototype")
    public RecordsTabContent recordsTabContent() {
        return new RecordsTabContent();
    }

    @Bean
    @Scope("prototype")
    public ReviewsTabContent reviewsTabContent() {
        return new ReviewsTabContent();
    }

    @Bean
    @Scope("prototype")
    public TracksTabContent tracksTabContent() {
        return new TracksTabContent();
    }

    @Bean
    @Scope("prototype")
    public UsersTabContent usersTabContent() {
        return new UsersTabContent();
    }

    @Bean
    @Scope("prototype")
    public VenuesTabContent venuesTabContent() {
        return new VenuesTabContent();
    }

    @Bean
    public EntityTabContentFactory entityTabContentFactory() {
        return new EntityTabContentFactory() {

            @Override
            public AboutTabContent createAboutTabContent() {
                return aboutTabContent();
            }

            @Override
            public BandsTabContent createBandsTabContent() {
                return bandsTabContent();
            }

            @Override
            public EventsTabContent createEventsTabContent() {
                return eventsTabContent();
            }

            @Override
            public NewsfeedTabContent createNewsfeedTabContent() {
                return newsfeedTabContent();
            }

            @Override
            public PhotosTabContent createPhotosTabContent() {
                return photosTabContent();
            }

            @Override
            public RecommendationsTabContent createRecomendationsTabContent() {
                return recommendationsTabContent();
            }

            @Override
            public RecordsTabContent createRecordsTabContent() {
                return recordsTabContent();
            }

            @Override
            public ReviewsTabContent createReviewsTabContent() {
                return reviewsTabContent();
            }

            @Override
            public TracksTabContent createTracksTabContent() {
                return tracksTabContent();
            }

            @Override
            public UsersTabContent createUsersTabContent() {
                return usersTabContent();
            }

            @Override
            public VenuesTabContent createVenuesTabContent() {
                return venuesTabContent();
            }

        };
    }

}
