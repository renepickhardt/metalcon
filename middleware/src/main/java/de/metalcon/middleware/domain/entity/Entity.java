package de.metalcon.middleware.domain.entity;

public abstract class Entity {
    
    public abstract EntityType getEntityType();
    
    private String name;
    
    public Entity(String name) {
        setName(name);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

}
