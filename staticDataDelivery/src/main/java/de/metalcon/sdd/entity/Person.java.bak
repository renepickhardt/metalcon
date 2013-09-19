package de.metalcon.sdd.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.server.Server;

public class Person extends Entity {
    
    private String name;
    
    private String firstname;
    
    private String lastname;
    
    private String url;
    
    private String birthday;
    
    private City city;
    
    public Person(Server server) {
        super(server);
    }
    
    @Override
    public void loadFromJson(String json) {
        Map<String, String> entity = parseJson(json);
        
        setId(new Muid(entity.get("id")));
        firstname = entity.get("firstname");
        lastname = entity.get("lastname");
        url = entity.get("url");
        birthday = entity.get("birthday");
        city = new City(server);
        city.loadFromId(new Muid(entity.get("city")));
        loadAfter();
    }
    
    public void loadFromCreateParams(Map<String, String[]> params) {
        setId(new Muid(getParam(params, "id")));
        firstname = getParam(params, "firstname");
        lastname = getParam(params, "lastname");
        url = getParam(params, "url");
        birthday = getParam(params, "birthday");
        city = new City(server);
        city.loadFromId(new Muid(getParam(params, "city")));
        loadAfter();
    }
    
    public void loadFromUpdateParams(Map<String, String[]> params) {
        Muid id = new Muid(getParam(params, "id"));
        loadFromId(id);
        
        String firstname = getParam(params, "firstname", true);
        if (firstname != null)
            this.firstname = firstname;
        String lastname = getParam(params, "lastname", true);
        if (lastname != null)
            this.lastname = lastname;
        String url = getParam(params, "url", true);
        if (url != null)
            this.url = url;
        String birthday = getParam(params, "birthday", true);
        if (birthday != null)
            this.birthday = birthday;
        String city = getParam(params, "city", true);
        if (city != null) {
            this.city = new City(server);
            this.city.loadFromId(new Muid(city));
        }
        loadAfter();
    }
    
    public void loadAfter() {
        name = firstname + " " + lastname;
    }
    
    @Override
    protected void generateJson() {
        Map<String, Object> j;
        
        // FULL
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("firstname", firstname);
        j.put("lastname", lastname);
        j.put("url", url);
        j.put("birthday", birthday);
        j.put("city", city.getId().toString());
        json.put(Detail.FULL, JSONValue.toJSONString(j));
        
        // SYMBOL
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        j.put("firstname", firstname);
        j.put("lastname", lastname);
        j.put("url", url);
        json.put(Detail.SYMBOL, JSONValue.toJSONString(j));
        
        // LINE 
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        j.put("firstname", firstname);
        j.put("lastname", lastname);
        j.put("url", url);
        j.put("birthday", birthday);
        j.put("city", new JsonString(city.getJson(Detail.SYMBOL)));
        json.put(Detail.LINE, JSONValue.toJSONString(j));
        
        // PARAGRAPH
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.PARAGRAPH, JSONValue.toJSONString(j));
        
        // PROFILE
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.PROFILE, JSONValue.toJSONString(j));
        
        // TOOLTIP
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.TOOLTIP, JSONValue.toJSONString(j));
        
        // SEARCH_ENTRY
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.SEARCH_ENTRY, JSONValue.toJSONString(j));
        
        // SEARCH_DETAILED
        j = new LinkedHashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.SEARCH_DETAILED, JSONValue.toJSONString(j));
    }

}
