package de.metalcon.sdd;

import de.metalcon.sdd.server.Server;

public class Detail {
    
    @SuppressWarnings("unused")
    private Server server;

    private String detail;
    
    public Detail(Server server, String detail) {
        if (server == null || detail == null)
            // TODO: handle this
            throw new RuntimeException();
        
        if (!server.config.details.contains(detail))
            // TODO: handle this
            throw new RuntimeException();
        
        this.detail = detail;
    }
    
    @Override
    public String toString() {
        return detail;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Detail o = (Detail) other;
        return detail.equals(o.detail);
    }
    
    @Override
    public int hashCode() {
        int hash = 45352;
        int mult = 677;
        
        hash = hash*mult + detail.hashCode();
        
        return hash;
    }
    
}
