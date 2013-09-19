package de.metalcon.sdd.generator;

public class EntityAttribute {

    private String name;
    
    private String type;
    
    private boolean hasArray;
    
    public EntityAttribute(String name, String type) {
        // unallowed names for entity attributes are:
        // server, jsonGenerated, json, id
        if (name.equals("server") ||
            name.equals("jsonGenerated") ||
            name.equals("json") ||
            name.equals("id")) {
            // TODO: handle this
            throw new RuntimeException();
        }
        
        this.name = name;
        
        if (type.substring(type.length() - 2).equals("[]")) {
            this.type = type.substring(0, type.length() - 2);
            hasArray = true;
        } else {
            this.type = type;
            hasArray = false;
        }
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    
    public boolean hasPrimitiveType() {
        return type.equals("String");
    }

    public boolean hasArray() {
        return hasArray;
    }
    
}
