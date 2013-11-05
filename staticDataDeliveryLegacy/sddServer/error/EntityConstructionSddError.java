package de.metalcon.sddServer.error;

public class EntityConstructionSddError extends SddError {

    private static final long serialVersionUID = -4325871321985715960L;
    
    String clazz;
    
    public EntityConstructionSddError(String clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getDescription() {
        return "Constructing Entity class failed.";
    }

    @Override
    public String getSuggestion() {
        return "The entity class that was tried to construct was \"" + clazz + "\"\n" +
               "Make sure it's constructor fits the syntax of the Entity constructor.";
    }

}
