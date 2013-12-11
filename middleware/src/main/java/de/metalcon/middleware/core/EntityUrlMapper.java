package de.metalcon.middleware.core;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.exception.RedirectException;

@Component
public class EntityUrlMapper {
    
    // Controller Mappings
    public static final String USER_MAPPING       = "/user/{path1}"                  + "{path2:}{path3:}";
    public static final String BAND_MAPPING       = "/music/{path1}"                 + "{path2:}{path3:}";
    public static final String RECORD_MAPPING     = "/music/{path1}/{path2}"                 + "{path3:}";
    public static final String TRACK_MAPPING      = "/music/{path1}/{path2}/{path3}";
    public static final String CITY_MAPPING       = "/city/{path1}"                  + "{path2:}{path3:}";
    public static final String VENUE_MAPPING      = "/venue/{path1}"                 + "{path2:}{path3:}";
    public static final String EVENT_MAPPING      = "/event/{path1}"                 + "{path2:}{path3:}";
    public static final String GENRE_MAPPING      = "/genre/{path1}"                 + "{path2:}{path3:}";
    public static final String INSTRUMENT_MAPPING = "/instrument/{path1}"            + "{path2:}{path3:}";
    public static final String TOUR_MAPPING       = "/tour/{path1}"                  + "{path2:}{path3:}";
    
    // Tab Mappings
    public static final String EMPTY_TAB_MAPPING = "";
    
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
