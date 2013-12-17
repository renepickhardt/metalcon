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
    public AboutTabContent aboutTab() {
        return new AboutTabContent();
    }

    @Bean
    @Scope("prototype")
    public BandsTabContent bandsTab() {
        return new BandsTabContent();
    }

    @Bean
    @Scope("prototype")
    public EventsTabContent eventsTab() {
        return new EventsTabContent();
    }

    @Bean
    @Scope("prototype")
    public NewsfeedTabContent newsfeedTab() {
        return new NewsfeedTabContent();
    }

    @Bean
    @Scope("prototype")
    public PhotosTabContent photosTab() {
        return new PhotosTabContent();
    }

    @Bean
    @Scope("prototype")
    public RecommendationsTabContent recommendationsTab() {
        return new RecommendationsTabContent();
    }

    @Bean
    @Scope("prototype")
    public RecordsTabContent recordsTab() {
        return new RecordsTabContent();
    }

    @Bean
    @Scope("prototype")
    public ReviewsTabContent reviewsTab() {
        return new ReviewsTabContent();
    }

    @Bean
    @Scope("prototype")
    public TracksTabContent tracksTab() {
        return new TracksTabContent();
    }

    @Bean
    @Scope("prototype")
    public UsersTabContent usersTab() {
        return new UsersTabContent();
    }

    @Bean
    @Scope("prototype")
    public VenuesTabContent venuesTab() {
        return new VenuesTabContent();
    }

    @Bean
    public EntityTabContentManager entityTabManager() {
        return new EntityTabContentManager() {

            @Override
            public AboutTabContent createAboutTab() {
                return aboutTab();
            }

            @Override
            public BandsTabContent createBandsTab() {
                return bandsTab();
            }

            @Override
            public EventsTabContent createEventsTab() {
                return eventsTab();
            }

            @Override
            public NewsfeedTabContent createNewsfeedTab() {
                return newsfeedTab();
            }

            @Override
            public PhotosTabContent createPhotosTab() {
                return photosTab();
            }

            @Override
            public RecommendationsTabContent createRecomendationsTab() {
                return recommendationsTab();
            }

            @Override
            public RecordsTabContent createRecordsTab() {
                return recordsTab();
            }

            @Override
            public ReviewsTabContent createReviewsTab() {
                return reviewsTab();
            }

            @Override
            public TracksTabContent createTracksTab() {
                return tracksTab();
            }

            @Override
            public UsersTabContent createUsersTab() {
                return usersTab();
            }

            @Override
            public VenuesTabContent createVenuesTab() {
                return venuesTab();
            }

        };
    }

}
