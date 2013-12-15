package de.metalcon.middleware.controller;

public class UrlMappings {
    
    // Entity Mappings
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
    
    // Entity Tab Mappings
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

}
