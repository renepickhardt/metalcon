package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;

public abstract class EntityViewManager {
    
    public EntityView createView(EntityType entityType) {
        switch (entityType) {
            case USER:       return createUserView();
            case BAND:       return createBandView();
            case RECORD:     return createRecordView();
            case TRACK:      return createTrackView();
            case VENUE:      return createVenueView();
            case EVENT:      return createEventView();
            case CITY:       return createCityView();
            case GENRE:      return createGenreView();
            case INSTRUMENT: return createInstrumentView();
            case TOUR:       return createTourView();
            
            default:
                throw new IllegalStateException("Unimplented EntityType: "
                        + entityType.toString() + ".");
        }
    }
    
    public abstract UserView createUserView();
    
    public abstract BandView createBandView();
    
    public abstract RecordView createRecordView();
    
    public abstract TrackView createTrackView();
    
    public abstract VenueView createVenueView();

    public abstract EventView createEventView();

    public abstract CityView createCityView();

    public abstract GenreView createGenreView();

    public abstract InstrumentView createInstrumentView();

    public abstract TourView createTourView();

}
