package de.metalcon.sdd.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.common.JsonOrderedFactory;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.server.Server;

public abstract class Entity {
    
    protected boolean jsonGenerated;
    
    protected Map<Detail, String> json;
    
    private String id;
    
    public Entity() {
        jsonGenerated = false;
        json = new LinkedHashMap<Detail, String>();
    }
    
    public void loadFromId(String id, Server server) {
        String json = server.readEntity(new IdDetail(id, Detail.FULL));
        loadFromJson(json, server);
    }
    
    public abstract void loadFromJson(String json, Server server);
    
    public abstract void loadFromCreateParams(Map<String, String[]> params,
                                              Server server);
    
    protected static String getParam(Map<String, String[]> params, String key) {
        String[] vals = params.get(key);
        if (vals != null) {
            String val = vals[0];
            if (val != null)
                return val;
        }
        
        // TODO: handle this
        throw new RuntimeException();
    }
    
    protected static Map<String, String> parseJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            @SuppressWarnings("unchecked")
            Map<String, String> entity = (Map<String, String>)
                    parser.parse(json, new JsonOrderedFactory());
            return entity;
        } catch (ParseException e) {
            // TODO: handle this
            throw new RuntimeException();
        } catch (ClassCastException e) {
            // TODO: handle this
            throw new RuntimeException();
        }
    }

    protected abstract void generateJson();
    
    public String getJson(Detail detail) {
        if (!jsonGenerated)
            generateJson();
        
        return json.get(detail);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    
}
