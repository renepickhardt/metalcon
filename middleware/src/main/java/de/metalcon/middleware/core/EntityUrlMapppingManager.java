package de.metalcon.middleware.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.domain.entity.impl.Band;
import de.metalcon.middleware.domain.entity.impl.City;
import de.metalcon.middleware.domain.entity.impl.Event;
import de.metalcon.middleware.domain.entity.impl.Genre;
import de.metalcon.middleware.domain.entity.impl.Instrument;
import de.metalcon.middleware.domain.entity.impl.Record;
import de.metalcon.middleware.domain.entity.impl.Tour;
import de.metalcon.middleware.domain.entity.impl.Track;
import de.metalcon.middleware.domain.entity.impl.User;
import de.metalcon.middleware.domain.entity.impl.Venue;
import de.metalcon.middleware.exception.RedirectException;

@Component
public class EntityUrlMapppingManager {

    public static final String WORD_SEPERATOR = "-";

    public static final String EMPTY_ENTITY = "_";

    private static final Logger logger = LoggerFactory
            .getLogger(EntityUrlMapppingManager.class);

    @Autowired
    private EntityManager entityManager;

    private Map<Muid, Set<String>> muidToMapping;

    private Map<String, Muid> mappingToMuidBand;

    private Map<String, Muid> mappingToMuidCity;

    private Map<String, Muid> mappingToMuidEvent;

    private Map<String, Muid> mappingToMuidGenre;

    private Map<String, Muid> mappingToMuidInstrument;

    private Map<Muid, Map<String, Muid>> mappingToMuidRecord;

    private Map<String, Muid> mappingToMuidTour;

    private Map<Muid, Map<String, Muid>> mappingToMuidTrack;

    private Map<String, Muid> mappingToMuidUser;

    private Map<String, Muid> mappingToMuidVenue;

    public EntityUrlMapppingManager() {
        muidToMapping = new HashMap<Muid, Set<String>>();

        mappingToMuidBand = new HashMap<String, Muid>();
        mappingToMuidCity = new HashMap<String, Muid>();
        mappingToMuidEvent = new HashMap<String, Muid>();
        mappingToMuidGenre = new HashMap<String, Muid>();
        mappingToMuidInstrument = new HashMap<String, Muid>();
        mappingToMuidRecord = new HashMap<Muid, Map<String, Muid>>();
        mappingToMuidTour = new HashMap<String, Muid>();
        mappingToMuidTrack = new HashMap<Muid, Map<String, Muid>>();
        mappingToMuidUser = new HashMap<String, Muid>();
        mappingToMuidVenue = new HashMap<String, Muid>();
    }

    public void registerMuid(Muid muid) {
        Entity entity = entityManager.getEntity(muid);
        switch (entity.getEntityType()) {
        // @formatter:off
            case BAND:       registerMuidBand      (muid, (Band)       entity); break;
            case CITY:       registerMuidCity      (muid, (City)       entity); break;
            case EVENT:      registerMuidEvent     (muid, (Event)      entity); break;
            case GENRE:      registerMuidGenre     (muid, (Genre)      entity); break;
            case INSTRUMENT: registerMuidInstrument(muid, (Instrument) entity); break;
            case RECORD:     registerMuidRecord    (muid, (Record)     entity); break;
            case TOUR:       registerMuidTour      (muid, (Tour)       entity); break;
            case TRACK:      registerMuidTrack     (muid, (Track)      entity); break;
            case USER:       registerMuidUser      (muid, (User)       entity); break;
            case VENUE:      registerMuidVenue     (muid, (Venue)      entity); break;
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplented EntityType: "
                        + entity.getEntityType().toString() + ".");
        }
    }

    public Muid getMuid(EntityType entityType, Map<String, String> pathVars)
            throws RedirectException {
        switch (entityType) {
        // @formatter:off
            case BAND:       return getMuidBand      (pathVars);
            case CITY:       return getMuidCity      (pathVars);
            case EVENT:      return getMuidEvent     (pathVars);
            case GENRE:      return getMuidGenre     (pathVars);
            case INSTRUMENT: return getMuidInstrument(pathVars);
            case RECORD:     return getMuidRecord    (pathVars);
            case TOUR:       return getMuidTour      (pathVars);
            case TRACK:      return getMuidTrack     (pathVars);
            case USER:       return getMuidUser      (pathVars);
            case VENUE:      return getMuidVenue     (pathVars);
            // @formatter:on

            default:
                throw new IllegalStateException("Unimplemented EntityType: "
                        + entityType.toString() + ".");
        }
    }

    public String getMapping(Muid muid) {
        Set<String> mappings = muidToMapping.get(muid);
        if (mappings == null || mappings.isEmpty())
            return null;
        return mappings.iterator().next();
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

    private void registerMappings(
            Map<String, Muid> mappingToMuid,
            Muid muid,
            Set<String> mappings) {
        Set<String> muidMappings = muidToMapping.get(muid);
        if (muidMappings == null)
            muidMappings = new LinkedHashSet<String>();

        String muidMapping =
                mappings.iterator().next() + WORD_SEPERATOR + muid.toString();
        mappingToMuid.put(muidMapping, muid);
        muidMappings.add(muidMapping);
        logMapping(muidMapping, muid);

        for (String mapping : mappings)
            if (!mappingToMuid.containsKey(mapping)) {
                mappingToMuid.put(mapping, muid);
                muidMappings.add(mapping);
                logMapping(mapping, muid);
            }
    }

    private void logMapping(String mapping, Muid muid) {
        logger.info("Mapped \"" + mapping + "\" to Entity \"" + muid.toString()
                + "\"");
    }

    private static String getPathVar(Map<String, String> pathVars, String var) {
        String val = pathVars.get(var);
        if (val == null)
            throw new IllegalStateException("Missing path variable: " + var
                    + ".");
        return val;
    }

    // registerMuid<Entity>

    private void registerMuidBand(Muid muid, Band band) {
        Set<String> mappings = new LinkedHashSet<String>();
        String name = toUrlText(band.getName());
        mappings.add(name);
        registerMappings(mappingToMuidBand, muid, mappings);
    }

    private void registerMuidCity(Muid muid, City city) {
        Set<String> mappings = new LinkedHashSet<String>();
        String name = toUrlText(city.getName());
        mappings.add(name);
        registerMappings(mappingToMuidCity, muid, mappings);
    }

    private void registerMuidEvent(Muid muid, Event event) {
        Set<String> mappings = new LinkedHashSet<String>();
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
        Set<String> mappings = new LinkedHashSet<String>();
        String name = toUrlText(genre.getName());
        mappings.add(name);
        registerMappings(mappingToMuidGenre, muid, mappings);
    }

    private void registerMuidInstrument(Muid muid, Instrument instrument) {
        Set<String> mappings = new LinkedHashSet<String>();
        String name = toUrlText(instrument.getName());
        mappings.add(name);
        registerMappings(mappingToMuidInstrument, muid, mappings);
    }

    private void registerMuidRecord(Muid muid, Record record) {
        Set<String> mappings = new LinkedHashSet<String>();
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

    private void registerMuidTour(Muid muid, Tour tour) {
        Set<String> mappings = new LinkedHashSet<String>();
        String name = toUrlText(tour.getName());
        mappings.add(name);

        Integer year = tour.getYear();
        if (year != null) {
            String yearFormatted = year.toString();
            mappings.add(yearFormatted + WORD_SEPERATOR + name);
        }

        registerMappings(mappingToMuidTour, muid, mappings);
    }

    private void registerMuidTrack(Muid muid, Track track) {
        Set<String> mappings = new LinkedHashSet<String>();
        Map<String, Muid> mappingToMuid;

        Muid record = track.getRecord();
        Muid band = track.getBand();
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

    private void registerMuidUser(Muid muid, User user) {
        Set<String> mappings = new LinkedHashSet<String>();
        String firstName = toUrlText(user.getFirstName());
        String lastName = toUrlText(user.getLastName());
        mappings.add(firstName + WORD_SEPERATOR + lastName);
        registerMappings(mappingToMuidUser, muid, mappings);
    }

    private void registerMuidVenue(Muid muid, Venue venue) {
        Set<String> mappings = new LinkedHashSet<String>();
        String venueName = toUrlText(venue.getName());
        mappings.add(venueName);

        Muid cityMuid = venue.getCity();
        if (cityMuid != null) {
            City city = (City) entityManager.getEntity(cityMuid);
            String cityName = toUrlText(city.getName());
            mappings.add(venueName + WORD_SEPERATOR + cityName);
        }

        registerMappings(mappingToMuidVenue, cityMuid, mappings);
    }

    // getMuid<Entity>

    private Muid getMuidBand(Map<String, String> pathVars)
            throws RedirectException {
        String pathBand = getPathVar(pathVars, "pathBand");
        if (pathBand.equals(EMPTY_ENTITY))
            return Muid.EMPTY_MUID;
        return mappingToMuidBand.get(pathBand);
    }

    private Muid getMuidCity(Map<String, String> pathVars)
            throws RedirectException {
        String pathCity = getPathVar(pathVars, "pathCity");
        return mappingToMuidCity.get(pathCity);
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

    private Muid getMuidTour(Map<String, String> pathVars)
            throws RedirectException {
        String pathTour = getPathVar(pathVars, "pathTour");
        return mappingToMuidTour.get(pathTour);
    }

    private Muid getMuidTrack(Map<String, String> pathVars)
            throws RedirectException {
        String pathTrack = getPathVar(pathVars, "pathTrack");
        Map<String, Muid> record =
                mappingToMuidTrack.get(getMuidRecord(pathVars));
        if (record == null)
            return null;
        return record.get(pathTrack);
    }

    private Muid getMuidUser(Map<String, String> pathVars)
            throws RedirectException {
        String pathUser = getPathVar(pathVars, "pathUser");
        return mappingToMuidUser.get(pathUser);
    }

    private Muid getMuidVenue(Map<String, String> pathVars)
            throws RedirectException {
        String pathVenue = getPathVar(pathVars, "pathVenue");
        return mappingToMuidVenue.get(pathVenue);
    }

}
