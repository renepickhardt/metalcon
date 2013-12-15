package de.metalcon.middleware.controller.entity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.core.EntityManager;
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
    public static final String USER_MAPPING       = "/user/{pathUser}";
    public static final String BAND_MAPPING       = "/music/{pathBand}";
    public static final String RECORD_MAPPING     = BAND_MAPPING + "/{pathRecord}";
    public static final String TRACK_MAPPING      = RECORD_MAPPING + "/{pathTrack}";
    public static final String CITY_MAPPING       = "/city/{pathCity}";
    public static final String VENUE_MAPPING      = "/venue/{pathVenue}";
    public static final String EVENT_MAPPING      = "/event/{pathEvent}";
    public static final String GENRE_MAPPING      = "/genre/{pathGenre}";
    public static final String INSTRUMENT_MAPPING = "/instrument/{pathInstrument}";
    public static final String TOUR_MAPPING       = "/tour/{pathTour}";
    
    // Tab Mappings
    public static final String ABOUT_TAB_MAPPING           = "/about";
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
    
    public static final String WORD_SEPERATOR = "-";
    
    public static final String EMPTY_ENTITY = "_";
    
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
    
    @Autowired
    private EntityManager entityManager;
    
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
        Muid muid = entity.getMuid();
        switch (entity.getEntityType()) {
            case USER:       registerMuidUser(muid, (User) entity);             break;
            case BAND:       registerMuidBand(muid, (Band) entity);             break;
            case RECORD:     registerMuidRecord(muid, (Record) entity);         break;
            case TRACK:      registerMuidTrack(muid, (Track) entity);           break;
            case VENUE:      registerMuidVenue(muid, (Venue) entity);           break;
            case EVENT:      registerMuidEvent(muid, (Event) entity);           break;
            case CITY:       registerMuidCity(muid, (City) entity);             break;
            case GENRE:      registerMuidGenre(muid, (Genre) entity);           break;
            case INSTRUMENT: registerMuidInstrument(muid, (Instrument) entity); break;
            case TOUR:       registerMuidTour(muid, (Tour) entity);             break;
            
            default:
                throw new IllegalStateException(
                        "Unimplented EntityType in EntityUrlMapper.registerMuid(): "
                                + entity.getEntityType().toString() + ".");
        }
    }
    
    public Muid getMuid(
            EntityType entityType, Map<String, String> pathVars)
    throws RedirectException {
        switch (entityType) {
            case USER:       return getMuidUser(pathVars);
            case BAND:       return getMuidBand(pathVars);
            case RECORD:     return getMuidRecord(pathVars);
            case TRACK:      return getMuidTrack(pathVars);
            case VENUE:      return getMuidVenue(pathVars);
            case EVENT:      return getMuidEvent(pathVars);
            case CITY:       return getMuidCity(pathVars);
            case GENRE:      return getMuidGenre(pathVars);
            case INSTRUMENT: return getMuidInstrument(pathVars);
            case TOUR:       return getMuidTour(pathVars);

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
    
    
    private static String toUrlText(String text) {
        String urlText = text;
        // Remove non letter characters. (http://stackoverflow.com/questions/1611979/remove-all-non-word-characters-from-a-string-in-java-leaving-accented-charact)
        urlText = urlText.replaceAll("[^\\p{L}\\p{Nd} ]", "");
        // Convert whitespace to WORD_SEPERATOR
        urlText = urlText.trim();
        urlText = urlText.replaceAll("\\s+", WORD_SEPERATOR); 
        return urlText;
    }
    
    private void registerMappings(Map<String, Muid> mappingToMuid, Muid muid,
            List<String> mappings) {
        for (String mapping : mappings) {
            mappingToMuid.put(mapping, muid);
            System.out.println(mapping);
        }
        mappingToMuid.put(
                mappings.get(0) + WORD_SEPERATOR + muid.toString(), muid);
    }
    
    private static String getPathVar(Map<String, String> pathVars, String var) {
        String val = pathVars.get(var);
        if (val == null)
            throw new IllegalStateException("Missing path variable: " + var + ".");
        return val;
    }
    
    
    // registerMuid<Entity>
    
    private void registerMuidUser(Muid muid, User user) {
        List<String> mappings = new LinkedList<String>();
        String firstName = toUrlText(user.getFirstName());
        String lastName  = toUrlText(user.getLastName());
        mappings.add(firstName + WORD_SEPERATOR + lastName);
        registerMappings(mappingToMuidUser, muid, mappings);
    }
     
    private void registerMuidBand(Muid muid, Band band) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(band.getName());
        mappings.add(name);
        registerMappings(mappingToMuidBand, muid, mappings);
    }
    
    private void registerMuidRecord(Muid muid, Record record) {
        List<String> mappings = new LinkedList<String>();
        Map<String, Muid> mappingToMuid;
        
        Muid band = record.getBand();
        if (band != null) {
            if (mappingToMuidRecord.containsKey(band))
                mappingToMuid = mappingToMuidRecord.get(band);
            else {
                mappingToMuid = new HashMap<String, Muid>();
                mappingToMuidRecord.put(band, mappingToMuid);
            }
        } else {
            mappingToMuid = new HashMap<String, Muid>();
            mappingToMuidRecord.put(Muid.EMPTY_MUID, mappingToMuid);
        }
        
        String name = toUrlText(record.getName());
        mappings.add(name);
        
        Integer year = record.getReleaseYear();
        if (year != null) {
            String yearFormatted = year.toString();
            mappings.add(yearFormatted + WORD_SEPERATOR + name);
        }
        
        registerMappings(mappingToMuid, muid, mappings);
    }
    
    private void registerMuidTrack(Muid muid, Track track) {
        List<String> mappings = new LinkedList<String>();
        Map<String, Muid> mappingToMuid;
        
        Muid record = track.getRecord();
        Muid band   = track.getBand();
        if (record != null) {
            if (mappingToMuidTrack.containsKey(record))
                mappingToMuid = mappingToMuidTrack.get(record);
            else {
                mappingToMuid = new HashMap<String, Muid>();
                mappingToMuidTrack.put(record, mappingToMuid);
            }
        } else if (band != null) {
            if (mappingToMuidTrack.containsKey(band))
                mappingToMuid = mappingToMuidTrack.get(band);
            else {
                mappingToMuid = new HashMap<String, Muid>();
                mappingToMuidTrack.put(band, mappingToMuid);
            }
        } else {
            mappingToMuid = new HashMap<String, Muid>();
            mappingToMuidTrack.put(Muid.EMPTY_MUID, mappingToMuid);
        }
        
        String name = toUrlText(track.getName());
        mappings.add(name);
        
        Integer trackNumber = track.getTrackNumber();
        if (trackNumber != null) {
            String trackNumberFormatted = String.format("%02d", trackNumber);
            mappings.add(trackNumberFormatted + WORD_SEPERATOR + name);
        }
        
        registerMappings(mappingToMuid, muid, mappings);
    }
    
    private void registerMuidCity(Muid muid, City city) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(city.getName());
        mappings.add(name);
        registerMappings(mappingToMuidCity, muid, mappings);
    }
    
    private void registerMuidVenue(Muid muid, Venue venue) {
        List<String> mappings = new LinkedList<String>();
        String venueName = toUrlText(venue.getName());
        mappings.add(venueName);
        
        Muid cityMuid = venue.getCity();
        if (cityMuid != null) {
            City city = (City) entityManager.getEntity(cityMuid);
            String cityName  = toUrlText(city.getName());
            mappings.add(venueName + WORD_SEPERATOR + cityName);
        }
        
        registerMappings(mappingToMuidVenue, cityMuid, mappings);
    }
    
    private void registerMuidEvent(Muid muid, Event event) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(event.getName());
        mappings.add(name);
            
        Date date = event.getDate();
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateFormatted = dateFormat.format(date);
            mappings.add(dateFormatted + WORD_SEPERATOR + name);
        }
        
        registerMappings(mappingToMuidEvent, muid, mappings);
    }
    
    private void registerMuidGenre(Muid muid, Genre genre) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(genre.getName());
        mappings.add(name);
        registerMappings(mappingToMuidGenre, muid, mappings);
    }
    
    private void registerMuidInstrument(Muid muid, Instrument instrument) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(instrument.getName());
        mappings.add(name);
        registerMappings(mappingToMuidInstrument, muid, mappings);
    }
    
    private void registerMuidTour(Muid muid, Tour tour) {
        List<String> mappings = new LinkedList<String>();
        String name = toUrlText(tour.getName());
        mappings.add(name);
        
        Integer year = tour.getYear();
        if (year != null) {
            String yearFormatted = year.toString();
            mappings.add(yearFormatted + WORD_SEPERATOR + name);
        }
        
        registerMappings(mappingToMuidTour, muid, mappings);
    }
    
    
    // getMuid<Entity>
    
    private Muid getMuidUser(Map<String, String> pathVars)
    throws RedirectException {
        String pathUser = getPathVar(pathVars, "pathUser");
        return mappingToMuidUser.get(pathUser);
    }
   
    private Muid getMuidBand(Map<String, String> pathVars)
    throws RedirectException {
        String pathBand = getPathVar(pathVars, "pathBand");
        if (pathBand.equals(EMPTY_ENTITY))
            return Muid.EMPTY_MUID;
        return mappingToMuidBand.get(pathBand);
    }
    
    private Muid getMuidRecord(Map<String, String> pathVars)
    throws RedirectException {
        String pathRecord = getPathVar(pathVars, "pathRecord");
        if (pathRecord.equals(EMPTY_ENTITY))
            return getMuidBand(pathVars);
        Map<String, Muid> band = mappingToMuidRecord.get(getMuidBand(pathVars));
        if (band == null)
            return null;
        return band.get(pathRecord);
    }
    
    private Muid getMuidTrack(Map<String, String> pathVars)
    throws RedirectException {
        String pathTrack  = getPathVar(pathVars, "pathTrack");
        Map<String, Muid> record = mappingToMuidTrack.get(getMuidRecord(pathVars));
        if (record == null)
            return  null;
        return record.get(pathTrack);
    }
    
    private Muid getMuidCity(Map<String, String> pathVars)
    throws RedirectException {
        String pathCity = getPathVar(pathVars, "pathCity");
        return mappingToMuidCity.get(pathCity);
    }
    
    private Muid getMuidVenue(Map<String, String> pathVars)
    throws RedirectException {
        String pathVenue = getPathVar(pathVars, "path");
        return mappingToMuidVenue.get(pathVenue);
    }
    
    private Muid getMuidEvent(Map<String, String> pathVars)
    throws RedirectException {
        String pathEvent = getPathVar(pathVars, "pathEvent");
        return mappingToMuidEvent.get(pathEvent);
    }
    
    private Muid getMuidGenre(Map<String, String> pathVars)
    throws RedirectException {
        String pathGenre = getPathVar(pathVars, "pathGenre");
        return mappingToMuidGenre.get(pathGenre);
    }
    
    private Muid getMuidInstrument(Map<String, String> pathVars)
    throws RedirectException {
        String pathInstrument = getPathVar(pathVars, "pathInstrument");
        return mappingToMuidInstrument.get(pathInstrument);
    }
    
    private Muid getMuidTour(Map<String, String> pathVars)
    throws RedirectException {
        String pathTour = getPathVar(pathVars, "pathTour");
        return mappingToMuidTour.get(pathTour);
    }
    
}
