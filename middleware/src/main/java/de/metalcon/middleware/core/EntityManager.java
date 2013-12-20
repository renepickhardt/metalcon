package de.metalcon.middleware.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;

/**
 * Manager to access entities. Abstracts away caching and potential
 * communication to backend components.
 */
@Component
public class EntityManager {

    /**
     * Map containing all currently cached entities.
     */
    private Map<Muid, Entity> entities;

    /**
     * Create the EntityManager.
     */
    public EntityManager() {
        entities = new HashMap<Muid, Entity>();
    }

    /**
     * Get a certain entity.
     * 
     * @param muid
     *            Entity identifier.
     * @return Entity referenced by that identifier.
     */
    public Entity getEntity(Muid muid) {
        return entities.get(muid);
    }

    /**
     * Get a certain entity. And make sure it's of correct type.
     * 
     * @param muid
     *            Entity identifier.
     * @param expected
     *            Expected entity type.
     * @return Entity referenced by that identifier.
     * @throws IllegalArgumentException
     *             If the identifier does not have the expected type.
     */
    public Entity getEntity(Muid muid, EntityType expectedType) {
        if (muid.getEntityType() != expectedType)
            throw new IllegalArgumentException("entity type is not "
                    + expectedType + " but " + muid.getEntityType());
        return getEntity(muid);
    }

    /**
     * Add an entity to be accessed using the manager later.
     * TODO: Remove this method. Entities should not have to be stored inside
     * the manager. Rather newly created entities are automatically stored, and
     * old ones are cached or accessed by talking to backend components.
     * 
     * @param entity
     *            Entity to be accessible.
     */
    public void putEntity(Entity entity) {
        entities.put(entity.getMuid(), entity);
    }

}
