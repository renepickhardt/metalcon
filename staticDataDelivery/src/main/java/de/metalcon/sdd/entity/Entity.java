package de.metalcon.sdd.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
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
    }
    
    public void loadFromId(Muid id) {
        String json = server.readEntity(new IdDetail(id, Detail.FULL));
        loadFromJson(json);
    }
    
    public abstract void loadFromJson(String json);
    
    public abstract void loadFromCreateParams(Map<String, String[]> params);
    
    public abstract void loadFromUpdateParams(Map<String, String[]> params);
    
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
    
    public Muid getId() {
        return id;
    }

    public void setId(Muid id) {
        this.id = id;
    }
    
    protected static String joinIds(List<Muid> ids) {
        String result = "";
        Boolean first = true;
        for (Muid id : ids) {
            if (first) {
                first = false;
                result += id.toString();
            } else
                result += "," + id.toString();
        }
        return result;
    }
    
}
