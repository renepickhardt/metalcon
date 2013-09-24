package de.metalcon.common;

public class Muid {
    
    private String id;
    
    public Muid() {
        // TODO: generate new MUID
        id = "b21d399ff7835a28"; 
    }
    
    public Muid(String id) {
        fromString(id);
    }
    
    public void fromString(String id) {
        // TODO: check if is valid MUID
        this.id = id;
    }
    
    public String toString() {
        return id;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        else if (this == other)
            return true;
        else if (other instanceof Muid)
            return id == ((Muid) other).id;
        return false;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
}
