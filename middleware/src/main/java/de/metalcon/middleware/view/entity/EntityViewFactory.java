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

public abstract class EntityViewFactory {

    public EntityView createView(EntityType entityType) {
        switch (entityType) {
        // @formatter:off
            case BAND:       return createBandView();
            case CITY:       return createCityView();
            case EVENT:      return createEventView();
            case GENRE:      return createGenreView();
            case INSTRUMENT: return createInstrumentView();
            case RECORD:     return createRecordView();
            case TOUR:       return createTourView();
            case TRACK:      return createTrackView();
            case USER:       return createUserView();
            case VENUE:      return createVenueView();
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplented EntityType: "
                        + entityType.toString() + ".");
        }
    }

    public abstract BandView createBandView();

    public abstract CityView createCityView();

    public abstract EventView createEventView();

    public abstract GenreView createGenreView();

    public abstract InstrumentView createInstrumentView();

    public abstract RecordView createRecordView();

    public abstract TourView createTourView();

    public abstract TrackView createTrackView();

    public abstract UserView createUserView();

    public abstract VenueView createVenueView();

}
