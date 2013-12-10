package de.metalcon.middleware.util;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.exception.RedirectException;

@Component
public class UrlMapper {
    
    // Controller Mappings
    
    public static final String USER_MAPPING       = "/user";
    public static final String MUSIC_MAPPPING     = "/music";
    public static final String CITY_MAPPING       = "/city";
    public static final String VENUE_MAPPING      = "/venue";
    public static final String EVENT_MAPPING      = "/event";
    public static final String GENRE_MAPPING      = "/genre";
    public static final String INSTRUMENT_MAPPING = "/instrument";
    public static final String TOUR_MAPPING       = "/tour";
    
    // Controller Method Mappings
    
    // Music
    public static final String BAND_MAPPING   = "/{pathBand}";
    public static final String RECORD_MAPPING = BAND_MAPPING + "/{pathRecord}";
    public static final String TRACK_MAPPING  = RECORD_MAPPING + "/{pathTrack}";
    
    
    public Muid getUser(String pathUser)
    throws RedirectException {
        return null;
    }
    
    public Muid getBand(String pathBand)
    throws RedirectException {
        return null;
    }
    
    public Muid getRecord(String pathBand, String pathRecord)
    throws RedirectException {
        return null;
    }
    
    public Muid getTrack(String pathBand, String pathRecord, String pathTrack)
    throws RedirectException {
        return null;
    }
    
    public Muid getCity(String pathCity)
    throws RedirectException {
        return null;
    }
    
    public Muid getVenue(String pathVenue)
    throws RedirectException {
        return null;
    }
    
    public Muid getEvent(String pathEvent)
    throws RedirectException {
        return null;
    }
    
    public Muid getGenre(String pathGenre)
    throws RedirectException {
        return null;
    }
    
    public Muid getInstrument(String pathInstrument)
    throws RedirectException {
        return null;
    }
    
    public Muid getTour(String pathTour)
    throws RedirectException {
        return null;
    }
    
}
