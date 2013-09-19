package de.metalcon.sdd.entity;

import de.metalcon.sdd.server.Server;

public class EntityByType {
    
    public static Entity newEntityByType(String type, Server server) {
        switch (type) {
//###SWITCH
            
            default:
                // TODO: handle this
                throw new RuntimeException();
        }
    }
    
}