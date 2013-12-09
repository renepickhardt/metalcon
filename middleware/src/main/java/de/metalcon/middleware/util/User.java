package de.metalcon.middleware.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class User {
    
    private Date date;
    
    public User() {
        date = new Date();
    }
    
    public String getDate() {
        DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        return format.format(date);
    }

}
