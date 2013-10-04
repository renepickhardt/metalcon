package de.metalcon.common;

public class Muid {
    
    private String id;
    
    public Muid(String id) {
        if (id == null)
            // TODO: handle this
            throw new RuntimeException();
        // TODO: check if is valid MUID
        this.id = id;
    }
    
    @Override
    public String toString() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        Muid o = (Muid) other;
        return id.equals(o.id);
    }
    
    @Override
    public int hashCode() {
        int hash = 9823;
        int mult = 887;
        
        hash = hash*mult + id.hashCode();
        
        return hash;
    }
    
}
