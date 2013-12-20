package de.metalcon.middleware.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.Entity;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.domain.entity.impl.Band;
import de.metalcon.middleware.domain.entity.impl.City;
import de.metalcon.middleware.domain.entity.impl.Event;

/**
 * manager to access single entities
 */
@Component
public class EntityManager {

	/**
	 * map containing each metalcon entity
	 */
	private Map<Muid, Entity> entities;

	/**
	 * create an empty entity manager
	 */
	public EntityManager() {
		this.entities = new HashMap<Muid, Entity>();
	}

	/**
	 * get a certain entity
	 * 
	 * @deprecated Use entity type specific methods such as getBand instead.
	 * @param muid
	 *            entity identifier
	 * @return entity having the identifier passed
	 */
	// TODO: remove method
	@Deprecated
	public Entity getEntity(Muid muid) {
		return this.entities.get(muid);
	}

	/**
	 * get a certain band
	 * 
	 * @param muid
	 *            band identifier
	 * @return band having the identifier passed
	 * @throws IllegalArgumentException
	 *             if identifier passed is not a band identifier
	 */
	public Band getBand(Muid muid) {
		return (Band) this.getEntity(muid, EntityType.BAND);
	}

	/**
	 * get a certain city
	 * 
	 * @param muid
	 *            city identifier
	 * @return city having the identifier passed
	 * @throws IllegalArgumentException
	 *             if identifier passed is not a city identifier
	 */
	public City getCity(Muid muid) {
		return (City) this.getEntity(muid, EntityType.CITY);
	}

	/**
	 * get a certain event
	 * 
	 * @param muid
	 *            event identifier
	 * @return event having the identifier passed
	 * @throws IllegalArgumentException
	 *             if identifier passed is not an event identifier
	 */
	public Event getEvent(Muid muid) {
		return (Event) this.getEntity(muid, EntityType.EVENT);
	}

	// TODO: create getters for entity types missing

	/**
	 * get a certain entity
	 * 
	 * @param muid
	 *            entity identifier
	 * @param expected
	 *            entity type expected
	 * @return entity having the identifier passed
	 * @throws IllegalArgumentException
	 *             if the identifier does not have the type expected
	 */
	private Entity getEntity(Muid muid, EntityType expected) {
		if (muid.getEntityType() != expected) {
			throw new IllegalArgumentException("entity type is not " + expected
					+ " but " + muid.getEntityType());
		}
		return this.entities.get(muid);
	}

	/**
	 * add an entity to be accessed using the manager later
	 * 
	 * @param entity
	 *            entity to be accessible
	 */
	public void putEntity(Entity entity) {
		this.entities.put(entity.getMuid(), entity);
	}

}
