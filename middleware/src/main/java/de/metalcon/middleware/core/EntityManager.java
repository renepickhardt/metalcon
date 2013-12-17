package de.metalcon.middleware.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;

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

}
