package de.metalcon.common;

public enum EntityType {

    // ------------
    BAND,
    CITY,
    EVENT,
    GENRE,
    INSTRUMENT,
    MUSICIAN,
    PERSON,
    POST,
    RECORD,
    TOUR,
    TRACK,
    VENUE,
    // ------------
    NONE;
    
    public static EntityType stringToEnum(String type) {
        switch (type) {
            case "band":            return BAND;
            case "city":            return CITY;
            case "event":           return EVENT;
            case "genre":           return GENRE;
            case "instrument":      return INSTRUMENT;
            case "musician":        return MUSICIAN;
            case "person":          return PERSON;
            case "post":            return POST;
            case "record":          return RECORD;
            case "tour":            return TOUR;
            case "track":           return TRACK;
            case "venue":           return VENUE;
                
            case "none":
            default:
                return NONE;
        }
    }
    
    public static String enumToString(EntityType type) {
        switch (type) {
            case BAND:              return "band";
            case CITY:              return "city";
            case EVENT:             return "event";
            case GENRE:             return "genre";
            case INSTRUMENT:        return "instrument";
            case MUSICIAN:          return "musician";
            case PERSON:            return "person";
            case POST:              return "post";
            case RECORD:            return "record";
            case TOUR:              return "tour";
            case TRACK:             return "track";
            case VENUE:             return "venue";
                
            case NONE:
            default:
                return "none";
        }
    }
    
}
