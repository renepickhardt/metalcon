package de.metalcon.middleware.view.entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class EntityViewConfig {
    
    @Bean
    @Scope("prototype")
    public BandView bandView()
    throws Exception {
        return new BandView();
    }

    @Bean
    @Scope("prototype")
    public RecordView recordView()
    throws Exception {
        return new RecordView();
    }
    
    @Bean
    @Scope("prototype")
    public TrackView trackView()
    throws Exception {
        return new TrackView();
    }
    
    @Bean
    public EntityViewManager entityViewManager() {
        return new EntityViewManager() {
            
            @Override
            public BandView createBandView()
            throws Exception {
                return bandView();
            }
            
            @Override
            public RecordView createRecordView()
            throws Exception {
                return recordView();
            }
            
            @Override
            public TrackView createTrackView()
            throws Exception {
                return trackView();
            }
            
        };
    }
    
}
