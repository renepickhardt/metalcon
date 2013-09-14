package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.EntityType;
import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.entity.City;
import de.metalcon.sdd.entity.Entity;
import de.metalcon.sdd.entity.Person;
import de.metalcon.sdd.server.Server;

public class CreateRequest extends Request {
    
    public CreateRequest(Server server) {
        super(server);
    }
    
    @Override
    public Map<String, Object> run() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        if (server.addRequest(this)) {
            // TODO: good response
            result.put("status", "worked");
        } else {
            // TODO: bad response
            result.put("status", "didnt work");
        }
        
        return result;
    }
    
    @Override
    public void exec() {
        Entity entity;
        
        Muid id = new Muid(getParam("id"));
        
        // switch (getParam(Muid.getMuidType(id))) {
        switch (EntityType.stringToEnum(getParam("type"))) {
            case CITY:              entity = new City();            break;
            case PERSON:            entity = new Person();          break;
                
            case NONE:
            default:
                // TODO: handle this
                throw new RuntimeException();
        }
        
        entity.loadFromCreateParams(params, server);
        
        for (Detail detail : Detail.values()) {
            if (detail == Detail.NONE)
                continue;
            server.writeEntity(new IdDetail(id, detail),
                               entity.getJson(detail));
            // TODO: remove sysout
            System.out.println("--- " + new IdDetail(id, detail).toString());
            System.out.println(entity.getJson(detail));
        }
        server.commitWriteBatch();
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> params = new HashMap<String, String[]>();
//        Koblenz (City)
//        params.put("id",        new String[]{"2731c67201ae29ae"});
//        params.put("type",      new String[]{"city"});
//        params.put("name",      new String[]{"Koblenz"});
//        params.put("url",       new String[]{"/city/Koblenz"});
//        params.put("country",   new String[]{"Deutschland"});
//        Thrudvangr Rising (Person)
//        params.put("id",        new String[]{"ce0058ac39a33616"});
//        params.put("type",      new String[]{"person"});
//        params.put("firstname", new String[]{"Thrudvangr"});
//        params.put("lastname",  new String[]{"Rising"});
//        params.put("url",       new String[]{"/person/Thrudvangr+Rising"});
//        params.put("birthday",  new String[]{"1991-01-01"});
//        params.put("city",      new String[]{"2731c67201ae29ae"});
//        Helsinki (City)
        params.put("id",        new String[]{"11233033e2b36cff"});
        params.put("type",      new String[]{"city"});
        params.put("name",      new String[]{"Helsinki"});
        params.put("url",       new String[]{"/city/Helsinki"});
        params.put("country",   new String[]{"Finnland"});
        CreateRequest r = new CreateRequest(s);
        r.setParams(params);
        String json = JSONValue.toJSONString(r.run());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        Thread.sleep(100);
        
        s.stop();
    }
    
}
