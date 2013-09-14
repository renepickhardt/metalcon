package de.metalcon.sdd;

import de.metalcon.sdd.error.ReadRequestInvalidDetailError;
import de.metalcon.sdd.error.ReadRequestInvalidQueryError;

public class IdDetail {
    
    final public static char delimeter = ':';

    private String id;
    private Detail detail;
    
    public IdDetail(String id, Detail detail) {
        this.id = id;
        this.detail = detail;
    }
    
    public IdDetail(String idDetail) {
        fromString(idDetail);
    }
    
    public void fromString(String idDetail) {
        int colonPos = idDetail.indexOf(delimeter);
        if (colonPos == -1 || colonPos == 0
                || colonPos == idDetail.length() - 1)
            throw new ReadRequestInvalidQueryError();
        
        id     = idDetail.substring(0, colonPos);
        // TODO: validate id
        detail = Detail.stringToEnum(idDetail.substring(colonPos + 1));
        if (detail == Detail.NONE)
            throw new ReadRequestInvalidDetailError();
    }
    
    public String toString() {
        return id + delimeter + Detail.enumToString(detail);
    }
    
    public String getId() {
        return id;
    }
    
    public Detail getDetail() {
        return detail;
    }
    
}
