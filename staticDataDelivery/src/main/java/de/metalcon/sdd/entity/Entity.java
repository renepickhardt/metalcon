package de.metalcon.sdd.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
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

    public void setId(Muid id) {
        this.id = id;
    }
    
    protected abstract void generateJson();
    
    protected static String getParam(Map<String, String[]> params, String key) {
        return getParam(params, key, false);
    }
    
    protected static String getParam(Map<String, String[]> params, String key,
                                     boolean optional) {
        return Servlet.getParam(params, key, optional);
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
    
    // --- load ----------------------------------------------------------------
    
    protected <T> T loadPrimitive(Class<T> clazz, String value) {
        return clazz.cast(value);
    }
    
    protected <T extends Entity> T loadEntity(Class<T> clazz, String id) {
        if (id == null)
            return null;
        
        T entity = null;
        try {
            entity = clazz.getConstructor(Server.class).newInstance(server);
            entity.loadFromId(new Muid(id));
        } catch (IllegalAccessException | InstantiationException
                | InvocationTargetException | NoSuchMethodException e) {
            // TODO: handle this
            throw new RuntimeException();
        }
        return entity;
    }
    
    protected <T extends Entity> List<T> loadEntityArray(Class<T> clazz,
                                                         String ids) {
        List<T> array = new LinkedList<T>();
        if (ids != null)
            for (String id : ids.split(","))
                array.add(loadEntity(clazz, id));
        return array;
    }
    
    // --- generate ------------------------------------------------------------
    
    protected <T> String generatePrimitive(T value) {
        return value.toString();
    }
    
    protected <T extends Entity> JsonString generateEntity(T entity,
                                                           Detail detail) {
        if (entity == null)
            return null;
        
        return new JsonString(entity.getJson(detail));
    }
    
    protected <T extends Entity> List<JsonString> generateEntityArray(
            List<T> array, Detail detail) {
        List<JsonString> jsons = new LinkedList<JsonString>();
        for (T entity : array)
            jsons.add(generateEntity(entity, detail));
        return jsons;
    }
    
    protected <T extends Entity> String generateEntityId(T entity) {
        if (entity == null)
            return null;
        
        return entity.getId().toString();
    }
    
    protected <T extends Entity> String generateEntityArrayIds(
            List<T> entities) {
        List<String> ids = new LinkedList<String>();
        for (T entity : entities)
            ids.add(generateEntityId(entity));
        
        String result = "";
        Boolean first = true;
        for (String id : ids) {
            if (first) {
                first = false;
                result += id;
            } else
                result += "," + id;
        }
        return result;
    }
    
}
