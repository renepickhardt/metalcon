package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.view.entity.impl.BandView;
import de.metalcon.middleware.view.entity.impl.CityView;
import de.metalcon.middleware.view.entity.impl.EventView;
import de.metalcon.middleware.view.entity.impl.GenreView;
import de.metalcon.middleware.view.entity.impl.InstrumentView;
import de.metalcon.middleware.view.entity.impl.RecordView;
import de.metalcon.middleware.view.entity.impl.TourView;
import de.metalcon.middleware.view.entity.impl.TrackView;
import de.metalcon.middleware.view.entity.impl.UserView;
import de.metalcon.middleware.view.entity.impl.VenueView;

public abstract class EntityViewManager {

    public EntityView createView(EntityType entityType) {
        switch (entityType) {
        // @formatter:off
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
            // @formatter:on

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
