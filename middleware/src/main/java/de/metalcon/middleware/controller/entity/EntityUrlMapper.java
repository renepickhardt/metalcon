package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
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
    
    private Map<Muid, List<String>>      muidToMapping;
    
    private Map<String, Muid>            mappingToMuidUser;
    private Map<String, Muid>            mappingToMuidBand;
    private Map<Muid, Map<String, Muid>> mappingToMuidRecord;
    private Map<Muid, Map<String, Muid>> mappingToMuidTrack;
    private Map<String, Muid>            mappingToMuidCity;
    private Map<String, Muid>            mappingToMuidVenue;
    private Map<String, Muid>            mappingToMuidEvent;
    private Map<String, Muid>            mappingToMuidGenre;
    private Map<String, Muid>            mappingToMuidInstrument;
    private Map<String, Muid>            mappingToMuidTour;
    
    public EntityUrlMapper() {
        muidToMapping           = new HashMap<Muid, List<String>>();
        
        mappingToMuidUser       = new HashMap<String, Muid>();
        mappingToMuidBand       = new HashMap<String, Muid>();
        mappingToMuidRecord     = new HashMap<Muid, Map<String, Muid>>();
        mappingToMuidTrack      = new HashMap<Muid, Map<String, Muid>>();
        mappingToMuidCity       = new HashMap<String, Muid>();
        mappingToMuidVenue      = new HashMap<String, Muid>();
        mappingToMuidEvent      = new HashMap<String, Muid>();
        mappingToMuidGenre      = new HashMap<String, Muid>();
        mappingToMuidInstrument = new HashMap<String, Muid>();
        mappingToMuidTour       = new HashMap<String, Muid>();
    }
    
    public void registerMuid(Muid muid) {
    }

    public Muid getMuid(
            EntityType entityType, String path1, String path2, String path3)
    throws RedirectException {
        switch (entityType) {
            case USER:       return getMuidUser(path1);
            case BAND:       return getMuidBand(path1);
            case RECORD:     return getMuidRecord(path1, path2);
            case TRACK:      return getMuidTrack(path1, path2, path3);
            case VENUE:      return getMuidVenue(path1);
            case EVENT:      return getMuidEvent(path1);
            case CITY:       return getMuidCity(path1);
            case GENRE:      return getMuidGenre(path1);
            case INSTRUMENT: return getMuidInstrument(path1);
            case TOUR:       return getMuidTour(path1);

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityType for controller.");
        }
    }
    
    public String getMapping(Muid muid) {
        List<String> mappings = muidToMapping.get(muid);
        if (mappings == null || mappings.isEmpty())
            return null;
        return mappings.get(0);
    }
    
    private Muid getMuidUser(String pathUser)
    throws RedirectException {
        return mappingToMuidUser.get(pathUser);
    }
    
    private Muid getMuidBand(String pathBand)
    throws RedirectException {
        return mappingToMuidBand.get(pathBand);
    }
    
    private Muid getMuidRecord(String pathBand, String pathRecord)
    throws RedirectException {
        return mappingToMuidRecord.get(getMuidBand(pathBand))
                                  .get(pathRecord);
    }
    
    private Muid getMuidTrack(String pathBand, String pathRecord,
            String pathTrack)
    throws RedirectException {
        return mappingToMuidTrack
                .get(getMuidRecord(pathBand, pathRecord))
                .get(pathTrack);
    }
    
    private Muid getMuidCity(String pathCity)
    throws RedirectException {
        return mappingToMuidCity.get(pathCity);
    }
    
    private Muid getMuidVenue(String pathVenue)
    throws RedirectException {
        return mappingToMuidVenue.get(pathVenue);
    }
    
    private Muid getMuidEvent(String pathEvent)
    throws RedirectException {
        return mappingToMuidEvent.get(pathEvent);
    }
    
    private Muid getMuidGenre(String pathGenre)
    throws RedirectException {
        return mappingToMuidGenre.get(pathGenre);
    }
    
    private Muid getMuidInstrument(String pathInstrument)
    throws RedirectException {
        return mappingToMuidInstrument.get(pathInstrument);
    }
    
    private Muid getMuidTour(String pathTour)
    throws RedirectException {
        return mappingToMuidTour.get(pathTour);
    }
    
}
