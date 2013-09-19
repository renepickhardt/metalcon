package de.metalcon.sdd.entity;

import de.metalcon.sdd.server.Server;

public class EntityByType {

    public static Entity newEntityByType(String type, Server server) {
        switch (type) {
            case "Band":
                return new Band(server);
            case "City":
                return new City(server);
            case "Disc":
                return new Disc(server);
            case "Event":
                return new Event(server);
            case "Genre":
                return new Genre(server);
            case "Instrument":
                return new Instrument(server);
            case "Musician":
                return new Musician(server);
            case "Person":
                return new Person(server);
            case "Record":
                return new Record(server);
            case "Tour":
                return new Tour(server);
            case "Track":
                return new Track(server);
            case "Venue":
                return new Venue(server);

            default:
                // TODO: handle this
                throw new RuntimeException();
        }
    }

}
