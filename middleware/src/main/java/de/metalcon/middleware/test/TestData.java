package de.metalcon.middleware.test;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.core.EntityManager;
import de.metalcon.middleware.core.EntityUrlMapppingManager;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.impl.Band;
import de.metalcon.middleware.domain.entity.impl.City;
import de.metalcon.middleware.domain.entity.impl.Event;
import de.metalcon.middleware.domain.entity.impl.Genre;
import de.metalcon.middleware.domain.entity.impl.Instrument;
import de.metalcon.middleware.domain.entity.impl.Record;
import de.metalcon.middleware.domain.entity.impl.Tour;
import de.metalcon.middleware.domain.entity.impl.Track;
import de.metalcon.middleware.domain.entity.impl.User;
import de.metalcon.middleware.domain.entity.impl.Venue;

@Component
public class TestData {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityUrlMapppingManager entityUrlMappingManager;

    @PostConstruct
    private void init() {
        Muid jamesHetfieldMuid = new Muid(11);
        Muid ensiferumMuid = new Muid(12);
        Muid ensiferum2Muid = new Muid(22);
        Muid victorySongsMuid = new Muid(13);
        Muid ahtiMuid = new Muid(14);
        Muid druckkammerMuid = new Muid(15);
        Muid wackenMuid = new Muid(16);
        Muid koblenzMuid = new Muid(17);
        Muid blackMetalMuid = new Muid(18);
        Muid guitarMuid = new Muid(19);
        Muid heidenfestMuid = new Muid(10);

        User jamesHetfield = new User(jamesHetfieldMuid, "James", "Hetfield");
        Band ensiferum = new Band(ensiferumMuid, "Ensiferum");
        Band ensiferum2 = new Band(ensiferum2Muid, "Ensiferum");
        Record victorySongs = new Record(victorySongsMuid, "Victory Songs");
        Track ahti = new Track(ahtiMuid, "Ahti");
        Venue druckkammer = new Venue(druckkammerMuid, "Druckkammer");
        Event wacken = new Event(wackenMuid, "Wacken");
        City koblenz = new City(koblenzMuid, "Koblenz");
        Genre blackMetal = new Genre(blackMetalMuid, "Black Metal");
        Instrument guitar = new Instrument(guitarMuid, "Guitar");
        Tour heidenfest = new Tour(heidenfestMuid, "Heidenfest");

        entityManager.putEntity(jamesHetfield);
        entityManager.putEntity(ensiferum);
        entityManager.putEntity(ensiferum2);
        entityManager.putEntity(victorySongs);
        entityManager.putEntity(ahti);
        entityManager.putEntity(druckkammer);
        entityManager.putEntity(wacken);
        entityManager.putEntity(koblenz);
        entityManager.putEntity(blackMetal);
        entityManager.putEntity(guitar);
        entityManager.putEntity(heidenfest);

        victorySongs.setBand(ensiferumMuid);
        victorySongs.setReleaseYear(2007);
        ahti.setBand(ensiferumMuid);
        ahti.setRecord(victorySongsMuid);
        ahti.setTrackNumber(4);
        druckkammer.setCity(koblenzMuid);
        wacken.setCity(koblenzMuid);
        wacken.setVenue(druckkammerMuid);
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 7, 31);
        wacken.setDate(cal.getTime());

        entityUrlMappingManager.registerMuid(jamesHetfieldMuid);
        entityUrlMappingManager.registerMuid(ensiferumMuid);
        entityUrlMappingManager.registerMuid(ensiferum2Muid);
        entityUrlMappingManager.registerMuid(victorySongsMuid);
        entityUrlMappingManager.registerMuid(ahtiMuid);
        entityUrlMappingManager.registerMuid(druckkammerMuid);
        entityUrlMappingManager.registerMuid(wackenMuid);
        entityUrlMappingManager.registerMuid(koblenzMuid);
        entityUrlMappingManager.registerMuid(blackMetalMuid);
        entityUrlMappingManager.registerMuid(guitarMuid);
        entityUrlMappingManager.registerMuid(heidenfestMuid);
    }

}
