package de.metalcon.middleware.test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.core.EntityManager;
import de.metalcon.middleware.core.EntityUrlMapppingManager;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Band;
import de.metalcon.middleware.domain.entity.Record;
import de.metalcon.middleware.domain.entity.Track;

@Component
public class TestData {

    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private EntityUrlMapppingManager entityUrlMappingManager;
    
    @PostConstruct
    private void init() {
        Muid ensiferumMuid    = new Muid(12);
        Muid ensiferum2Muid   = new Muid(22);
        Muid victorySongsMuid = new Muid(13);
        Muid ahtiMuid         = new Muid(14);
      
        Band   ensiferum    = new Band(ensiferumMuid, "Ensiferum");
        Band   ensiferum2   = new Band(ensiferum2Muid, "Ensiferum");
        Record victorySongs = new Record(victorySongsMuid, "Victory Songs");
        Track ahti          = new Track(ahtiMuid, "Ahti");
      
        entityManager.putEntity(ensiferum);
        entityManager.putEntity(ensiferum2);
        entityManager.putEntity(victorySongs);
        entityManager.putEntity(ahti);
      
        victorySongs.setBand(ensiferumMuid);
        victorySongs.setReleaseYear(2007);
        ahti.setBand(ensiferumMuid);
        ahti.setRecord(victorySongsMuid);
        ahti.setTrackNumber(4);
      
        entityUrlMappingManager.registerMuid(ensiferumMuid);
        entityUrlMappingManager.registerMuid(ensiferum2Muid);
        entityUrlMappingManager.registerMuid(victorySongsMuid);
        entityUrlMappingManager.registerMuid(ahtiMuid);
    }
    
}
