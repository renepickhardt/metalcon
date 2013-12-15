package de.metalcon.middleware.core;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.metalcon.middleware.controller.entity.EntityUrlMapper;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Band;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.Record;
import de.metalcon.middleware.domain.entity.Track;

@Component
public class EntityManager {

    private Map<Muid, Entity> entities;
    
    public EntityManager() {
        entities = new HashMap<Muid, Entity>();
    }
    
    public Entity getEntity(Muid muid) {
        return entities.get(muid);
    }
    
    public void putEntity(Entity entity) {
        entities.put(entity.getMuid(), entity);
    }
    
    // TODO: remove
    @Autowired
    private EntityUrlMapper entityUrlMapper;
    @PostConstruct
    private void fillWithTestData() {
        Muid ensiferumMuid    = new Muid(1);
        Muid victorySongsMuid = new Muid(2);
        Muid ahtiMuid         = new Muid(3);
        
        Band ensiferum = new Band(ensiferumMuid, "Ensiferum");
        Record victorySongs = new Record(victorySongsMuid, "Victory Songs");
        Track ahti = new Track(ahtiMuid, "Ahti");
        
        putEntity(ensiferum);
        putEntity(victorySongs);
        putEntity(ahti);
        
        victorySongs.setBand(ensiferumMuid);
        victorySongs.setReleaseYear(2007);
        ahti.setBand(ensiferumMuid);
        ahti.setRecord(victorySongsMuid);
        
        entityUrlMapper.registerMuid(ensiferum);
        entityUrlMapper.registerMuid(victorySongs);
        entityUrlMapper.registerMuid(ahti);
    }
    
}
