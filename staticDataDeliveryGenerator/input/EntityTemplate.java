package de.metalcon.sdd.entity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.server.Server;

//public class EntityTemplate extends Entity {
//###CLASS

//###ATTRIBUTES
    
//###CONSTRUCTOR
    //public EntityTemplate(Server server) {
        super(server);
    }
    
    @Override
    public void loadFromJson(String json) {
        Map<String, String> entity = parseJson(json);
        
        setId(new Muid(entity.get("id")));
        
        String oid;
        
//###LOAD_FROM_JSON
    }
    
    @Override
    public void loadFromCreateParams(Map<String, String[]> params) {
        setId(new Muid(getParam(params, "id")));
        
        String oid;
        
//###LOAD_FROM_CREATE_PARAMS
    }
    
    @Override
    public void loadFromUpdateParams(Map<String, String[]> params) {
        Muid id = new Muid(getParam(params, "id"));
        loadFromId(id);
        
//###LOAD_FROM_UPDATE_PARAMS
    }
    
    @Override
    protected void generateJson() {
        Map<String, Object> j;
        List<Muid> ids;
        List<JsonString> os;
        
//###GENERATE_JSON
    }
    
}
