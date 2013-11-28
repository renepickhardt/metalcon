package de.metalcon.middleware.domain;

import java.util.Date;

public class Band extends Entity {
    
    private String name;
    
    private Date foundation;

    public Band(String name, Date foundation) {
        super();
        this.name = name;
        this.foundation = foundation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getFoundation() {
        return foundation;
    }

    public void setFoundation(Date foundation) {
        this.foundation = foundation;
    }
    
}
