package de.metalcon.middleware.domain.entity;

public class User extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }
    
    public static String concatNames(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
    
    private String firstName;
    
    private String lastName;
    
    public User(String firstName, String lastName) {
        super(concatNames(firstName, lastName));
        setFirstName(firstName);
        setLastName(lastName);
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
