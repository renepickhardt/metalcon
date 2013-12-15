package de.metalcon.middleware.view.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class EntityViewConfig {
    
    @Bean
    @Scope("prototype")
    public UserView userView() {
        return new UserView();
    }

    @Bean
    @Scope("prototype")
    public BandView bandView() {
        return new BandView();
    }

    @Bean
    @Scope("prototype")
    public RecordView recordView() {
        return new RecordView();
    }
    
    @Bean
    @Scope("prototype")
    public TrackView trackView() {
        return new TrackView();
    }
    
    @Bean
    @Scope("prototype")
    public VenueView venueView() {
        return new VenueView();
    }

    @Bean
    @Scope("prototype")
    public EventView eventView() {
        return new EventView();
    }

    @Bean
    @Scope("prototype")
    public CityView cityView() {
        return new CityView();
    }

    @Bean
    @Scope("prototype")
    public GenreView genreView() {
        return new GenreView();
    }

    @Bean
    @Scope("prototype")
    public InstrumentView instrumentView() {
        return new InstrumentView();
    }

    @Bean
    @Scope("prototype")
    public TourView tourView() {
        return new TourView();
    }

    @Bean
    public EntityViewManager entityViewManager() {
        return new EntityViewManager() {
            
            @Override
            public UserView createUserView() {
                return userView();
            }
            
            @Override
            public BandView createBandView() {
                return bandView();
            }
            
            @Override
            public RecordView createRecordView() {
                return recordView();
            }
            
            @Override
            public TrackView createTrackView() {
                return trackView();
            }
            
            @Override
            public VenueView createVenueView() {
                return venueView();
            }
            
            @Override
            public EventView createEventView() {
                return eventView();
            }
            
            @Override
            public CityView createCityView() {
                return cityView();
            }
            
            @Override
            public GenreView createGenreView() {
                return genreView();
            }
            
            @Override
            public InstrumentView createInstrumentView() {
                return instrumentView();
            }
            
            @Override
            public TourView createTourView() {
                return tourView();
            }
            
        };
    }
    
}
