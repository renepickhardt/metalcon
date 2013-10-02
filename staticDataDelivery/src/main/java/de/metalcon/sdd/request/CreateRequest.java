package de.metalcon.sdd.request;

import static de.metalcon.sdd.entity.EntityByType.newEntityByType;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.sdd.entity.Entity;
import de.metalcon.sdd.error.RequestMissingParamIdSddError;
import de.metalcon.sdd.error.RequestMissingParamTypeSddError;
import de.metalcon.sdd.server.Server;

public class CreateRequest extends Request {
    
    String paramId;
    
    String paramType;
    
    public CreateRequest(Server server) {
        super(server);
    }
    
    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        paramId = getParam("id");
        if (paramId == null)
            throw new RequestMissingParamIdSddError();
        paramType = getParam("type");
        if (paramType == null)
            throw new RequestMissingParamTypeSddError();
        
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
    public void runQueueAction() {
        Entity entity = newEntityByType(paramType, server);
//        Entity entity = Entity.newEntityByType(id.getType(), server);
        entity.loadFromCreateParams(params);
        server.writeEntity(entity);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        // --- old
//        Map<String, String[]> params = new HashMap<String, String[]>();
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
//        params.put("id",        new String[]{"11233033e2b36cff"});
//        params.put("type",      new String[]{"city"});
//        params.put("name",      new String[]{"Helsinki"});
//        params.put("url",       new String[]{"/city/Helsinki"});
//        params.put("country",   new String[]{"Finland"});
        // --- new
//        Map<String, String[]> params1 = new HashMap<String, String[]>();
//        params1.put("id",        new String[]{"2f364c13c0114e16"});
//        params1.put("type",      new String[]{"Musician"});
//        params1.put("name",      new String[]{"Marilyn Manson"});
//        params1.put("url",       new String[]{"/musician/Marilyn+Manson"});
//        params1.put("active",    new String[]{"true"});
//        params1.put("founder",   new String[]{"true"});
//        params1.put("spans",     new String[]{"1989-now"});
//        Map<String, String[]> params2 = new HashMap<String, String[]>();
//        params2.put("id",        new String[]{"11233033e2b36cff"});
//        params2.put("type",      new String[]{"Musician"});
//        params2.put("name",      new String[]{"Johan Hegg"});
//        params2.put("url",       new String[]{"/musician/Johan+Hegg"});
//        params2.put("active",    new String[]{"true"});
//        params2.put("founder",   new String[]{"true"});
//        params2.put("spans",     new String[]{"1992-now"});
//        Map<String, String[]> params3 = new HashMap<String, String[]>();
//        params3.put("id",        new String[]{"ce0058ac39a33616"});
//        params3.put("type",      new String[]{"Band"});
//        params3.put("name",      new String[]{"Ensiferum"});
//        params3.put("url",       new String[]{"/music/Ensiferum"});
//        params3.put("foundation",new String[]{"1995"});
//        params3.put("musicians", new String[]{"2f364c13c0114e16,11233033e2b36cff"});
//        Map<String, String[]> params4 = new HashMap<String, String[]>();
//        params4.put("id",        new String[]{"2f364c13c0114e16"});
//        params4.put("type",      new String[]{"Musician"});
//        params4.put("name",      new String[]{"Marilyyyn Manson"});
//        params4.put("url",       new String[]{"/musician/Marilyn+Manson"});
//        params4.put("active",    new String[]{"true"});
//        params4.put("founder",   new String[]{"true"});
//        params4.put("spans",     new String[]{"1989-now"});
//        
//        CreateRequest r;
//        String json;
//        
//        for (int i = 0; i != 1000; ++i) {
//            r = new CreateRequest(s);
//            r.setParams(params1);
//            json = JSONValue.toJSONString(r.runHttp());
//            json = JsonPrettyPrinter.prettyPrintJson(json);
//            System.out.println(json);
//        }
//        r = new CreateRequest(s);
//        r.setParams(params2);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        r = new CreateRequest(s);
//        r.setParams(params3);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        s.waitUntilQueueEmpty();
//        System.out.println(s.readEntity(new IdDetail(new Muid(
//                "ce0058ac39a33616"), Detail.PROFILE)));
//        r = new CreateRequest(s);
//        r.setParams(params4);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        s.waitUntilQueueEmpty();
//        System.out.println(s.readEntity(new IdDetail(new Muid(
//                "ce0058ac39a33616"), Detail.PROFILE)));
        
        long t = System.currentTimeMillis();
        
        for (int i = 0; i != 1000*1000; ++i) {
            Map<String, String[]> params = new HashMap<String, String[]>();
            params.put("id",        new String[]{i+""});
            params.put("type",      new String[]{"Musician"});
            params.put("name",      new String[]{"Musician " + i});
            params.put("url",       new String[]{"/musician/Musician" + i});
            params.put("active",    new String[]{"true"});
            params.put("founder",   new String[]{"true"});
            params.put("spans",     new String[]{"1989-now"});
            Request r = new CreateRequest(s);
            r.setParams(params);
            r.runHttp();
//            String json = JSONValue.toJSONString(r.runHttp());
//            json = JsonPrettyPrinter.prettyPrintJson(json);
//            System.out.println(json);
            if (i % 1000 == 0)
                System.out.println(((float) i) / (1000.0*1000.0) + "%");
        }
        
        s.waitUntilQueueEmpty();
        
        long tt = System.currentTimeMillis();
        
        System.out.println(tt - t + "ms");
        
        s.stop();
    }
    
}
