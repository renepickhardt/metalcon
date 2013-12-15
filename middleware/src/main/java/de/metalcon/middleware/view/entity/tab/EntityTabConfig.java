package de.metalcon.middleware.view.entity.tab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class EntityTabConfig {
    
    @Bean
    @Scope("prototype")
    public AboutTab aboutTab() {
        return new AboutTab();
    }
    
    @Bean
    @Scope("prototype")
    public NewsfeedTab newsfeedTab() {
        return new NewsfeedTab();
    }
    
    @Bean
    @Scope("prototype")
    public BandsTab bandsTab() {
        return new BandsTab();
    }
    
    @Bean
    @Scope("prototype")
    public RecordsTab recordsTab() {
        return new RecordsTab();
    }
    
    @Bean
    @Scope("prototype")
    public TracksTab tracksTab() {
        return new TracksTab();
    }
    
    @Bean
    @Scope("prototype")
    public ReviewsTab reviewsTab() {
        return new ReviewsTab();
    }
    
    @Bean
    @Scope("prototype")
    public VenuesTab venuesTab() {
        return new VenuesTab();
    }
    
    @Bean
    @Scope("prototype")
    public EventsTab eventsTab() {
        return new EventsTab();
    }
    
    @Bean
    @Scope("prototype")
    public UsersTab usersTab() {
        return new UsersTab();
    }
    
    @Bean
    @Scope("prototype")
    public PhotosTab photosTab() {
        return new PhotosTab();
    }
    
    @Bean
    @Scope("prototype")
    public RecommendationsTab recommendationsTab() {
        return new RecommendationsTab();
    }
    
    @Bean
    public EntityTabManager entityTabManager() {
        return new EntityTabManager() {
            
            @Override
            public AboutTab createAboutTab() {
                return aboutTab();
            }
            
            @Override
            public NewsfeedTab createNewsfeedTab() {
                return newsfeedTab();
            }
            
            @Override
            public BandsTab createBandsTab() {
                return bandsTab();
            }
            
            @Override
            public RecordsTab createRecordsTab() {
                return recordsTab();
            }
            
            @Override
            public TracksTab createTracksTab() {
                return tracksTab();
            }
            
            @Override
            public ReviewsTab createReviewsTab() {
                return reviewsTab();
            }
            
            @Override
            public VenuesTab createVenuesTab() {
                return venuesTab();
            }
            
            @Override
            public EventsTab createEventsTab() {
                return eventsTab();
            }
            
            @Override
            public UsersTab createUsersTab() {
                return usersTab();
            }
            
            @Override
            public PhotosTab createPhotosTab() {
                return photosTab();
            }
            
            @Override
            public RecommendationsTab createRecomendationsTab() {
                return recommendationsTab();
            }
            
        };
    }

}
