package de.metalcon.sddServer;

import de.metalcon.common.Muid;
import de.metalcon.sddServer.error.IdDetailInvalidStringSddError;
import de.metalcon.sddServer.server.Server;

public class IdDetail {
    
    private String delimeter;
    
    @SuppressWarnings("unused")
    private Server server;

    private Muid id;
    
    private Detail detail;
    
    public void init(Server server) {
        this.server = server;
        
        delimeter = server.config.getIdDetailDelimeter();
    }
    
    public IdDetail(Server server, Muid id, Detail detail) {
        if (server == null)
            throw new IllegalArgumentException("server was null");
        if (id == null)
            throw new IllegalArgumentException("id was null");
        if (detail == null)
            throw new IllegalArgumentException("detail was null");
        
        init(server);
        
        this.id = id;
        this.detail = detail;
    }
    
    public IdDetail(Server server, String idDetail) {
        if (server == null)
            throw new IllegalArgumentException("server was null");
        if (idDetail == null)
            throw new IllegalArgumentException("idDetail was null");
        
        init(server);
        
        int colonPos = idDetail.indexOf(delimeter);
        if (colonPos == -1 || colonPos == 0
                || colonPos == idDetail.length() - 1)
            throw new IdDetailInvalidStringSddError(idDetail);
        
        String idStr     = idDetail.substring(0, colonPos);
        String detailStr = idDetail.substring(colonPos + 1);
        
        id     = new Muid(idStr);
        detail = new Detail(server, detailStr);
    }
    
    @Override
    public String toString() {
        return id.toString() + delimeter + detail.toString();
    }
    
    public Muid getId() {
        return id;
    }
    
    public Detail getDetail() {
        return detail;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        IdDetail o = (IdDetail) other;
        return id.equals(o.id) && detail.equals(o.detail);
    }
    
    @Override
    public int hashCode() {
        int hash = 23895;
        int mult = 461;
        
        hash = hash*mult + id.hashCode();
        hash = hash*mult + detail.hashCode();
        
        return hash;
    }
    
}
