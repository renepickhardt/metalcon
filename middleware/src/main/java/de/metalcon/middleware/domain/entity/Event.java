package de.metalcon.middleware.domain.entity;

import java.util.Date;

public class Event extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.EVENT;
    }
    
    private City city;
    
    private Venue venue;
    
    private Date date;
    
    public Event(String name) {
        super(name);
        city  = null;
        venue = null;
        date  = null;
    }
    
    public City getCity() {
        return city;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    
    public Venue getVenue() {
        return venue;
    }
    
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
}
