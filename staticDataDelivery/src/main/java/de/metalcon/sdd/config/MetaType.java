package de.metalcon.sdd.config;

public class MetaType {

    final public static String arraySuffix = "[]";
    
    private String type;
    
    private boolean isArray;
    
    private boolean isPrimitive;
    
    public MetaType(String type) {
        if (type == null)
            // TODO: handle this
            throw new RuntimeException();
        
        this.type   = type;
        isArray     = false;
        isPrimitive = false;
        
        if (type.length() >= arraySuffix.length()
                && type.substring(type.length() - arraySuffix.length()).equals(
                        arraySuffix)) {
            this.type = type.substring(0, type.length() - arraySuffix.length());
            isArray = true;
        }
            
        if (type.equals("String"))
            isPrimitive = true;
    }
    
    public String getType() {
        return type;
    }
    
    public boolean isArray() {
        return isArray;
    }
    
    public boolean isPrimitive() {
        return isPrimitive;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        MetaType o = (MetaType) other;
        return type.equals(o.type) &&
               isArray == o.isArray &&
               isPrimitive == o.isPrimitive;
    }
    
    @Override
    public int hashCode() {
        int hash = 54294;
        int mult = 359;
        
        hash = hash*mult + type.hashCode();
        hash = hash*mult + (isArray ? 0 : 1);
        hash = hash*mult + (isPrimitive ? 0 : 1);
        
        return hash;
    }
    
}
