package de.metalcon.common;

public class Muid {
    
    private String id;
    
    public Muid(String id) {
        fromString(id);
    }
    
    public void fromString(String id) {
        this.id = id;
    }
    
    public String toString() {
        return id;
    }
    
    /**
     * Generates a MUID for a given EntityType.
     * @param type  The type the MUID should save.
     * @return A newly generated MUID.
     */
    public static String genereateMuid(EntityType type) {
        // TODO
        return "b21d399ff7835a28"; 
    }
    
    /**
     * Checks for a given ID, whether its a valid MUID or not.
     * @param muid  The ID to check.
     * @return `true` if valid, `false` if not.
     */
    public static boolean isValidMuid(String muid) {
        // TODO
        return true;
    }
    
    /**
     * Reads the EntityType out of a MUID.
     * @param muid  The MUID to read.
     * @return The EntityType of the MUID.
     */
    public static EntityType getMuidType(String muid) {
        // TODO
        return EntityType.PERSON;
    }
    
}
