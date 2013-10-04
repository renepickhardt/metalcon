package de.metalcon.sdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.config.MetaEntity;
import de.metalcon.sdd.config.MetaEntityOutput;
import de.metalcon.sdd.config.MetaType;
import de.metalcon.sdd.server.Server;
import de.metalcon.sdd.tomcat.Servlet;

public class Entity {

    private Server server;
    
    private Muid id;
    
    private String type;
    
    private MetaEntity meta;
    
    private Map<String, String> attrs;
    
    private Set<Muid> dependencies;
    
    private Map<Detail, String> jsons;
    
    public void init(Server server) {
        this.server  = server;
        id           = null;
        type         = null;
        meta         = null;
        attrs        = null;
        dependencies = new HashSet<Muid>();
        jsons        = new HashMap<Detail, String>();
    }
    
    public Entity(Server server, Muid id) {
        if (server == null || id == null)
            // TODO: handle this
            throw new RuntimeException();
        
        init(server);
        
        this.id = id;
        loadFromGraph(null);
        generateDependencies();
        generateJsons();
    }
    
    public Entity(Server server, Map<String, String[]> params) {
        if (server == null || params == null)
            // TODO: handle this
            throw new RuntimeException();
        
        init(server);
        
        String paramId = getParam(params, "id");
        if (paramId == null)
            // TODO: handle this
            throw new RuntimeException();
        
        String paramType = getParam(params, "type");
        if (paramType == null)
            // TODO: handle this
            throw new RuntimeException();
        
        id = new Muid(paramId);
        loadFromGraph(paramType);
        
        for (String attrName : params.keySet()) {
            if (!attrName.equals("id") &&
                !attrName.equals("muid") &&
                !attrName.equals("type")) {
                String attrValue = getParam(params, attrName);
                if (!meta.attrs.containsKey(attrName))
                    // TODO: handle this
                    throw new RuntimeException();
                attrs.put(attrName, attrValue);
            }
        }
        
        generateDependencies();
        generateJsons();
    }
    
    private void loadFromGraph(String paramType) {
        if (id == null)
            // TODO: handle this
            throw new RuntimeException();
       
        Map<String, String> attrs = server.readEntityGraph(id);
        
        if (attrs == null) { // entity is new for server
            if (paramType == null)
                // TODO: handle this (can't load non-existent entities by id)
                throw new RuntimeException();
            
            type = paramType;
            attrs = new HashMap<String, String>();
            attrs.put("type", type);
        } else { // entity already exists on server
            type = attrs.get("type");
            if (type == null)
                // TODO: move this sanity check into server startup
                throw new RuntimeException();
            else if (paramType != null && !paramType.equals(type))
                // TODO: either wipe attrs or throw exception
                throw new RuntimeException();
        }
        
        meta = server.config.entities.get(type);
        if (meta == null)
            // TODO: handle this (invalid type)
            throw new RuntimeException();
        
        // TODO: move this sanity check into server startup
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            String attrName = attr.getKey();
            if (!attrName.equals("muid") && !attrName.equals("type")) {
                MetaType attrType = meta.attrs.get(attrName);
                if (attrType == null)
                    // TODO: handle this (invalid attr)
                    throw new RuntimeException();
            }
        }
       
        this.attrs = attrs;
    }
    
    private void generateDependencies() {
        for (Map.Entry<String, MetaType> attr : meta.attrs.entrySet()) {
            String   attrValue = attrs.get(attr.getKey());
            MetaType attrType  = attr.getValue();
            if (!attrType.isPrimitive() && attrValue != null) {
                if (attrType.isArray())
                    for (String val : attrValue.split(","))
                        dependencies.add(new Muid(val));
                else
                    dependencies.add(new Muid(attrValue));
            }
        }
    }
    
    private void generateJsons() {
        for (String metaDetail : server.config.details) {
            Map<String, Object> json = new HashMap<String, Object>();
            json.put("id",   id.toString());
            json.put("type", type);
            
            MetaEntityOutput output = meta.output.get(metaDetail);
            if (output != null)
                for (Map.Entry<String, String> oattr :
                        output.oattrs.entrySet()) {
                    String attrName = oattr.getKey();
                    json.put(attrName, attrToJson(meta.attrs.get(attrName),
                                                  attrs.get(attrName),
                                                  oattr.getValue()));
                }
            
            jsons.put(new Detail(server, metaDetail),
                      JSONValue.toJSONString(json));
        }
    }
    
    private Object attrToJson(MetaType type, String value, String detail) {
        if (value == null)
            return null;
        
        if (type.isArray()) {
            List<Object> list = new LinkedList<Object>();
            for (String val : value.split(",")) {
                list.add(attrNonArrayToJson(type, val, detail));
            }
            return list;
        } else 
            return attrNonArrayToJson(type, value, detail);
    }
    
    private Object attrNonArrayToJson(MetaType type, String value,
                                      String detail) {
        if (type.isPrimitive()) {
            switch (type.getType()) {
                case "String": return value;
                
                default:
                    // TODO: handle this (unknown primitive type)
                    throw new RuntimeException();
            }
        } else {
            IdDetail idDetail = new IdDetail(server, new Muid(value),
                                             new Detail(server, detail));
            return new JsonString(server.readEntityJson(idDetail));
        }
    }
    
    public Muid getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public Map<String, String> getAttrs() {
        // Don't change return value of getAttrs as it will change Entity state.
        return attrs;
    }
    
    public Set<Muid> getDependencies() {
        // We copy dependencies so that changes to the return value of
        // getDpendencies() don't change dependencies.
        Set<Muid> result = new HashSet<Muid>();
        result.addAll(dependencies);
        return result;
    }
    
    public String getJson(Detail detail) {
        return jsons.get(detail);
    }
    
    private static String getParam(Map<String, String[]> params, String key) {
        return Servlet.getParam(params, key);
    }
    
}
