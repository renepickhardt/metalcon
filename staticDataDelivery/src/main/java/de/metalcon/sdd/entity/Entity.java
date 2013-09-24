package de.metalcon.sdd.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.error.EntityConstructionSddError;
import de.metalcon.sdd.error.EntityInvalidIDSddError;
import de.metalcon.sdd.error.EntityJsonClassCastSddError;
import de.metalcon.sdd.error.EntityJsonParseSddError;
import de.metalcon.sdd.error.EntityNoJsonForDetailSddError;
import de.metalcon.sdd.server.Server;
import de.metalcon.sdd.tomcat.Servlet;

public abstract class Entity {
    
    protected Server server;
    
    protected boolean jsonGenerated;
    
    protected Map<Detail, String> json;
    
    private Muid id;
    
    public Entity(Server server) {
        this.server = server;
        jsonGenerated = false;
        json = new HashMap<Detail, String>();
        id = null;
    }
    
    public abstract void loadFromJson(String json);
    
    public abstract void loadFromCreateParams(Map<String, String[]> params);
    
    public abstract void loadFromUpdateParams(Map<String, String[]> params);
    
    public void loadFromId(Muid id) {
        String json = server.readEntity(new IdDetail(id, Detail.FULL));
        if (json == null)
            throw new EntityInvalidIDSddError(id);
        loadFromJson(json);
    }
    
    public String getJson(Detail detail) {
        if (!jsonGenerated)
            generateJson();
        
        String result = json.get(detail);
        if (result == null)
            throw new EntityNoJsonForDetailSddError(detail);
        return result;
    }
    
    public Muid getId() {
        return id;
    }

    public abstract Set<Muid> getDependingOn();
    
    protected abstract void generateJson();
    
    protected void setId(Muid id) {
        this.id = id;
    }
    
    protected static String getParam(Map<String, String[]> params, String key) {
        return Servlet.getParam(params, key);
    }
    
    protected static Map<String, String> parseJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            @SuppressWarnings("unchecked")
            Map<String, String> entity = (Map<String, String>)
                                         parser.parse(json);
            return entity;
        } catch (ParseException e) {
            throw new EntityJsonParseSddError(e, json);
        } catch (ClassCastException e) {
            throw new EntityJsonClassCastSddError(json);
        }
    }
    
    protected static <T> void colAddIfNotNull(Collection<T> col, T value) {
        if (value != null)
            col.add(value);
    }
    
    protected static <T> void colAddIfNotNull(Collection<T> col1,
            Collection<T> col2) {
        if (col2 != null)
            for (T value : col2)
                colAddIfNotNull(col1, value);
    }
    
    protected static String idToString(Muid id) {
        if (id == null)
            return null;
        return id.toString();
    }
    
    protected static String idsToString(Collection<Muid> ids) {
        if (ids == null)
            return null;
        
        String result = "";
        boolean first = true;
        for (Muid id : ids) {
            if (first) {
                first = false;
                result += id.toString();
            } else
                result += "," + id.toString();
        }
        return result;
    }
    
    protected static <T extends Entity> Muid getEntityId(T entity) {
        if (entity == null)
            return null;
        
        return entity.getId();
    }
    
    protected static <T extends Entity> List<Muid> getEntityArrayIds(
            List<T> entities) {
        if (entities.isEmpty())
            return null;
        
        List<Muid> ids = new LinkedList<Muid>();
        for (T entity : entities)
            ids.add(getEntityId(entity));
        return ids;
    }
    
    // --- load ----------------------------------------------------------------
    
    protected <T> T loadPrimitive(Class<T> clazz, String value) {
        return loadPrimitive(clazz, value, null);
    }
    
    protected <T extends Entity> T loadEntity(Class<T> clazz, String id) {
        return loadEntity(clazz, id, null);
    }
    
    protected <T extends Entity> List<T> loadEntityArray(Class<T> clazz,
                                                         String ids) {
        return loadEntityArray(clazz, ids, null);
    }
    
    protected <T> T loadPrimitive(Class<T> clazz,
                                  String value, String oldValue) {
        if (value == null) {
            if (oldValue == null)
                return null;
            value = oldValue;
        }
                    
        return clazz.cast(value);
    }
    
    protected <T extends Entity> T loadEntity(Class<T> clazz,
                                              String id, String oldId) {
        if (id == null) {
            if (oldId == null)
                return null;
            id = oldId;
        }
        
        if (id.isEmpty())
            return null;
        
        T entity = null;
        try {
            entity = clazz.getConstructor(Server.class).newInstance(server);
            entity.loadFromId(new Muid(id));
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new EntityConstructionSddError(clazz.toString());
        }
        return entity;
    }
    
    protected <T extends Entity> List<T> loadEntityArray(Class<T> clazz,
                                                         String ids,
                                                         String oldIds) {
        if (ids == null) {
            if (oldIds == null)
                return new LinkedList<T>();
            ids = oldIds;
        }
        
        List<T> entities = new LinkedList<T>();
        for (String id : ids.split(",")) {
            T entity = loadEntity(clazz, id);
            if (entity != null)
                entities.add(entity);
        }
        return entities;
    }
    
    // --- generate ------------------------------------------------------------
    
    protected <T> String generatePrimitive(T value) {
        if (value == null)
            return null;
        
        return value.toString();
    }
    
    protected <T extends Entity> JsonString generateEntity(T entity,
                                                           Detail detail) {
        if (entity == null)
            return null;
        
        return new JsonString(entity.getJson(detail));
    }
    
    protected <T extends Entity> List<JsonString> generateEntityArray(
            List<T> entities, Detail detail) {
        if (entities.isEmpty())
            return null;
        
        List<JsonString> jsons = new LinkedList<JsonString>();
        for (T entity : entities)
            jsons.add(generateEntity(entity, detail));
        return jsons;
    }
    
}
