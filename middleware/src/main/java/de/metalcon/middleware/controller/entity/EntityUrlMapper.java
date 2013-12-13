package de.metalcon.middleware.controller.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Band;
import de.metalcon.middleware.domain.entity.City;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.domain.entity.Event;
import de.metalcon.middleware.domain.entity.Genre;
import de.metalcon.middleware.domain.entity.Instrument;
import de.metalcon.middleware.domain.entity.Record;
import de.metalcon.middleware.domain.entity.Tour;
import de.metalcon.middleware.domain.entity.Track;
import de.metalcon.middleware.domain.entity.User;
import de.metalcon.middleware.domain.entity.Venue;
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
    public static final String EMPTY_TAB_MAPPING           = "";
    public static final String INFO_TAB_MAPPING            = "/info";
    public static final String NEWSFEED_TAB_MAPPING        = "/news";
    public static final String BANDS_TAB_MAPPING           = "/bands";
    public static final String RECORDS_TAB_MAPPING         = "/records";
    public static final String TRACKS_TAB_MAPPING          = "/tracks";
    public static final String REVIEWS_TAB_MAPPING         = "/reviews";
    public static final String VENUES_TAB_MAPPING          = "/venues";
    public static final String EVENTS_TAB_MAPPING          = "/events";
    public static final String USERS_TAB_MAPPING           = "/users";
    public static final String PHOTOS_TAB_MAPPING          = "/photos";
    public static final String RECOMMENDATIONS_TAB_MAPPING = "/recommendations";
    
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
    
    public void registerMuid(Entity entity) {
        switch (entity.getEntityType()) {
            case USER:       registerMuidUser((User) entity);
            case BAND:       registerMuidBand((Band) entity);
            case RECORD:     registerMuidRecord((Record) entity);
            case TRACK:      registerMuidTrack((Track) entity);
            case VENUE:      registerMuidVenue((Venue) entity);
            case EVENT:      registerMuidEvent((Event) entity);
            case CITY:       registerMuidCity((City) entity);
            case GENRE:      registerMuidGenre((Genre) entity);
            case INSTRUMENT: registerMuidInstrument((Instrument) entity);
            case TOUR:       registerMuidTour((Tour) entity);
            
            default:
                throw new IllegalStateException(
                        "Unimplented EntityType in EntityUrlMapper.registerMuid(): "
                                + entity.getEntityType().toString() + ".");
        }
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
                        "Unimplemented EntityType in EntityUrlMapper.getMuid: "
                                + entityType.toString() + ".");
        }
    }
    
    public String getMapping(Muid muid) {
        List<String> mappings = muidToMapping.get(muid);
        if (mappings == null || mappings.isEmpty())
            return null;
        return mappings.get(0);
    }
    
    // User
    
    private void registerMuidUser(User user) {
    }
    
    private Muid getMuidUser(String pathUser)
    throws RedirectException {
        return mappingToMuidUser.get(pathUser);
    }
    
    // Band
    
    private void registerMuidBand(Band band) {
    }
    
    private Muid getMuidBand(String pathBand)
    throws RedirectException {
        return mappingToMuidBand.get(pathBand);
    }
    
    // Record
    
    private void registerMuidRecord(Record record) {
    }
    
    private Muid getMuidRecord(String pathBand, String pathRecord)
    throws RedirectException {
        Map<String, Muid> band = mappingToMuidRecord.get(pathBand);
        if (band == null)
            return null;
        return band.get(pathRecord);
    }
    
    // Track
    
    private void registerMuidTrack(Track track) {
    }
    
    private Muid getMuidTrack(String pathBand, String pathRecord,
            String pathTrack)
    throws RedirectException {
        Map<String, Muid> record =
                mappingToMuidTrack.get(getMuidRecord(pathBand, pathRecord));
        if (record == null)
            return  null;
        return record.get(pathTrack);
    }
    
    // City
    
    private void registerMuidCity(City city) {
    }
    
    private Muid getMuidCity(String pathCity)
    throws RedirectException {
        return mappingToMuidCity.get(pathCity);
    }
    
    // Venue
    
    private void registerMuidVenue(Venue venue) {
    }
    
    private Muid getMuidVenue(String pathVenue)
    throws RedirectException {
        return mappingToMuidVenue.get(pathVenue);
    }
    
    // Event
    
    private void registerMuidEvent(Event event) {
    }
    
    private Muid getMuidEvent(String pathEvent)
    throws RedirectException {
        return mappingToMuidEvent.get(pathEvent);
    }
    
    // Genre
    
    private void registerMuidGenre(Genre genre) {
    }
    
    private Muid getMuidGenre(String pathGenre)
    throws RedirectException {
        return mappingToMuidGenre.get(pathGenre);
    }
    
    // Instrument
    
    private void registerMuidInstrument(Instrument instrument) {
    }
    
    private Muid getMuidInstrument(String pathInstrument)
    throws RedirectException {
        return mappingToMuidInstrument.get(pathInstrument);
    }
    
    // Tour
    
    private void registerMuidTour(Tour tour) {
    }
    
    private Muid getMuidTour(String pathTour)
    throws RedirectException {
        return mappingToMuidTour.get(pathTour);
    }
    
}
