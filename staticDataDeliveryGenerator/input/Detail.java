package de.metalcon.sdd;

import de.metalcon.sdd.Detail;

public enum Detail {

    FULL,
//###ENUM
    NONE;
    
    public static Detail stringToEnum(String detail) {
        switch (detail) {
            case "full":
                return FULL;
//###STRING_TO_ENUM
            
            case "none":
            default:
                return NONE;
        }
    }
    
    public static String enumToString(Detail detail) {
        switch (detail) {
            case FULL:
                return "full";
//###ENUM_TO_STRING
            
            case NONE:
            default:
                return "none";
        }
    }
    
}