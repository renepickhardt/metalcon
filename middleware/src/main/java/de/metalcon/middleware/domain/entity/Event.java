package de.metalcon.middleware.domain.entity;

import java.util.Date;

import de.metalcon.middleware.domain.Muid;

public class Event extends Entity {

    @Override
    public EntityType getEntityType() {
        return EntityType.EVENT;
    }
    
    private Muid city;
    
    private Muid venue;
    
    private Date date;
    
    public Event(String name) {
        super(name);
        city  = null;
        venue = null;
        date  = null;
    }
    
    public Muid getCity() {
        return city;
    }
    
    public void setCity(Muid city) {
        this.city = city;
    }
    
    public Muid getVenue() {
        return venue;
    }
    
    public void setVenue(Muid venue) {
        this.venue = venue;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
}
