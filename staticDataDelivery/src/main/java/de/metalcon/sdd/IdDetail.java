package de.metalcon.sdd;

import de.metalcon.common.Muid;
import de.metalcon.sdd.error.IdDetailInvalidDetailSddError;
import de.metalcon.sdd.error.IdDetailInvalidStringSddError;

public class IdDetail {
    
    final public static char delimeter = ':';

    private Muid id;
    private Detail detail;
    
    public IdDetail(Muid id, Detail detail) {
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
            throw new IdDetailInvalidStringSddError(idDetail);
        
        String idStr     = idDetail.substring(0, colonPos);
        String detailStr = idDetail.substring(colonPos + 1);
        
        id     = new Muid(idStr);
        detail = Detail.stringToEnum(detailStr);
        if (detail == Detail.NONE)
            throw new IdDetailInvalidDetailSddError(detailStr);
    }
    
    public String toString() {
        return id.toString() + delimeter + Detail.enumToString(detail);
    }
    
    public Muid getId() {
        return id;
    }
    
    public Detail getDetail() {
        return detail;
    }
    
}
