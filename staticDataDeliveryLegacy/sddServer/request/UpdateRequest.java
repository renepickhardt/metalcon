package de.metalcon.sddServer.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.Muid;
import de.metalcon.sdd.queue.UpdateQueueAction;
import de.metalcon.sddServer.server.Server;

public class UpdateRequest extends Request {
    
    public UpdateRequest(Server server) {
        super(server);
    }

    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        String paramId = getParam("id");
        if (paramId == null)
            // TODO: handle this
            throw new RuntimeException();
        Muid id = new Muid(paramId);
        
        UpdateQueueAction action = new UpdateQueueAction(server, id, params);
        
        if (server.queueAction(action)) {
            // TODO: good response
            result.put("status", "worked");
        } else {
            // TODO: bad response
            result.put("status", "didnt work");
        }
        
        return result;
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> marilynManson = new HashMap<String, String[]>();
        marilynManson.put("id",        new String[]{"2f364c13c0114e16"});
        marilynManson.put("type",      new String[]{"Musician"});
        marilynManson.put("name",      new String[]{"Marilyn Manson"});
        marilynManson.put("url",       new String[]{"/musician/Marilyn+Manson"});
        marilynManson.put("active",    new String[]{"true"});
        marilynManson.put("founder",   new String[]{"true"});
        marilynManson.put("spans",     new String[]{"1989-now"});
        Map<String, String[]> johanHegg = new HashMap<String, String[]>();
        johanHegg.put("id",        new String[]{"11233033e2b36cff"});
        johanHegg.put("type",      new String[]{"Musician"});
        johanHegg.put("name",      new String[]{"Johan Hegg"});
        johanHegg.put("url",       new String[]{"/musician/Johan+Hegg"});
        johanHegg.put("active",    new String[]{"true"});
        johanHegg.put("founder",   new String[]{"true"});
        johanHegg.put("spans",     new String[]{"1992-now"});
        Map<String, String[]> ensiferum = new HashMap<String, String[]>();
        ensiferum.put("id",        new String[]{"ce0058ac39a33616"});
        ensiferum.put("type",      new String[]{"Band"});
        ensiferum.put("name",      new String[]{"Ensiferum"});
        ensiferum.put("url",       new String[]{"/music/Ensiferum"});
        ensiferum.put("foundation",new String[]{"1995"});
        ensiferum.put("musicians", new String[]{"2f364c13c0114e16,11233033e2b36cff"});
        Map<String, String[]> helsinki = new HashMap<String, String[]>();
        helsinki.put("id", new String[]{"ec94771fed93cffc"});
        helsinki.put("type", new String[]{"City"});
        helsinki.put("name", new String[]{"Koblenz"});
        Map<String, String[]> finntroll = new HashMap<String, String[]>();
        finntroll.put("id", new String[]{"664421a1b59263c5"});
        finntroll.put("type", new String[]{"Band"});
        finntroll.put("name", new String[]{"Finntroll"});
        finntroll.put("city", new String[]{"ec94771fed93cffc"});
        
        Map<String, String[]> band_01 = new HashMap<String, String[]>();
        band_01.put("id", new String[]{"band_01"});
        band_01.put("type", new String[]{"Band"});
        band_01.put("name", new String[]{"band_01"});
        Map<String, String[]> band_01depts = new HashMap<String, String[]>();
        band_01depts.put("id", new String[]{"band_01"});
        band_01depts.put("type", new String[]{"Band"});
        band_01depts.put("records", new String[]{"record_01,record_02"});
        Map<String, String[]> record_01 = new HashMap<>();
        record_01.put("id", new String[]{"record_01"});
        record_01.put("type", new String[]{"Record"});
        record_01.put("name", new String[]{"record_01"});
        record_01.put("band", new String[]{"band_01"});
//        Map<String, String[]> record_01depts = new HashMap<>();
//        record_01depts.put("id", new String[]{"record_01"});
//        record_01depts.put("type", new String[]{"Record"});
//        record_01depts.put("band", new String[]{"band_01"});
        Map<String, String[]> record_02 = new HashMap<>();
        record_02.put("id", new String[]{"record_02"});
        record_02.put("type", new String[]{"Record"});
        record_02.put("name", new String[]{"record_02"});
        record_02.put("band", new String[]{"band_01"});
//        Map<String, String[]> record_02depts = new HashMap<>();
//        record_02depts.put("id", new String[]{"record_02"});
//        record_02depts.put("type", new String[]{"Record"});
//        record_02depts.put("band", new String[]{"band_01"});
        
        UpdateRequest r;
        String json;
        // Create Entities
        r = new UpdateRequest(s);
        r.setParams(band_01);
        json = JSONValue.toJSONString(r.runHttp());
        System.out.println(json);
        r = new UpdateRequest(s);
        r.setParams(record_01);
        json = JSONValue.toJSONString(r.runHttp());
        System.out.println(json);
        r = new UpdateRequest(s);
        r.setParams(record_02);
        json = JSONValue.toJSONString(r.runHttp());
        System.out.println(json);
        // Create Relationships
        r = new UpdateRequest(s);
        r.setParams(band_01depts);
        json = JSONValue.toJSONString(r.runHttp());
        System.out.println(json);
//        r = new UpdateRequest(s);
//        r.setParams(record_01depts);
//        json = JSONValue.toJSONString(r.runHttp());
//        System.out.println(json);
//        r = new UpdateRequest(s);
//        r.setParams(record_02depts);
//        json = JSONValue.toJSONString(r.runHttp());
//        System.out.println(json);
        
//        UpdateRequest r;
//        String json;
//        r = new UpdateRequest(s);
//        r.setParams(helsinki);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        r = new UpdateRequest(s);
//        r.setParams(finntroll);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
        
//        r = new UpdateRequest(s);
//        r.setParams(marilynManson);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        r = new UpdateRequest(s);
//        r.setParams(johanHegg);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
//        r = new UpdateRequest(s);
//        r.setParams(ensiferum);
//        json = JSONValue.toJSONString(r.runHttp());
//        json = JsonPrettyPrinter.prettyPrintJson(json);
//        System.out.println(json);
        
        s.waitUntilQueueEmpty();
        s.stop();
    }
    
//    public static void main(String[] args) throws InterruptedException {
//        Server s = new Server();
//        s.start();
//        
//        // --- old
////        Map<String, String[]> params = new HashMap<String, String[]>();
////        Koblenz (City)
////        params.put("id",        new String[]{"2731c67201ae29ae"});
////        params.put("type",      new String[]{"city"});
////        params.put("name",      new String[]{"Koblenz"});
////        params.put("url",       new String[]{"/city/Koblenz"});
////        params.put("country",   new String[]{"Deutschland"});
////        Thrudvangr Rising (Person)
////        params.put("id",        new String[]{"ce0058ac39a33616"});
////        params.put("type",      new String[]{"person"});
////        params.put("firstname", new String[]{"Thrudvangr"});
////        params.put("lastname",  new String[]{"Rising"});
////        params.put("url",       new String[]{"/person/Thrudvangr+Rising"});
////        params.put("birthday",  new String[]{"1991-01-01"});
////        params.put("city",      new String[]{"2731c67201ae29ae"});
////        Helsinki (City)
////        params.put("id",        new String[]{"11233033e2b36cff"});
////        params.put("type",      new String[]{"city"});
////        params.put("name",      new String[]{"Helsinki"});
////        params.put("url",       new String[]{"/city/Helsinki"});
////        params.put("country",   new String[]{"Finland"});
//        // --- new
////        Map<String, String[]> params1 = new HashMap<String, String[]>();
////        params1.put("id",        new String[]{"2f364c13c0114e16"});
////        params1.put("type",      new String[]{"Musician"});
////        params1.put("name",      new String[]{"Marilyn Manson"});
////        params1.put("url",       new String[]{"/musician/Marilyn+Manson"});
////        params1.put("active",    new String[]{"true"});
////        params1.put("founder",   new String[]{"true"});
////        params1.put("spans",     new String[]{"1989-now"});
////        Map<String, String[]> params2 = new HashMap<String, String[]>();
////        params2.put("id",        new String[]{"11233033e2b36cff"});
////        params2.put("type",      new String[]{"Musician"});
////        params2.put("name",      new String[]{"Johan Hegg"});
////        params2.put("url",       new String[]{"/musician/Johan+Hegg"});
////        params2.put("active",    new String[]{"true"});
////        params2.put("founder",   new String[]{"true"});
////        params2.put("spans",     new String[]{"1992-now"});
////        Map<String, String[]> params3 = new HashMap<String, String[]>();
////        params3.put("id",        new String[]{"ce0058ac39a33616"});
////        params3.put("type",      new String[]{"Band"});
////        params3.put("name",      new String[]{"Ensiferum"});
////        params3.put("url",       new String[]{"/music/Ensiferum"});
////        params3.put("foundation",new String[]{"1995"});
////        params3.put("musicians", new String[]{"2f364c13c0114e16,11233033e2b36cff"});
////        Map<String, String[]> params4 = new HashMap<String, String[]>();
////        params4.put("id",        new String[]{"2f364c13c0114e16"});
////        params4.put("type",      new String[]{"Musician"});
////        params4.put("name",      new String[]{"Marilyyyn Manson"});
////        params4.put("url",       new String[]{"/musician/Marilyn+Manson"});
////        params4.put("active",    new String[]{"true"});
////        params4.put("founder",   new String[]{"true"});
////        params4.put("spans",     new String[]{"1989-now"});
////        
////        CreateRequest r;
////        String json;
////        
////        for (int i = 0; i != 1000; ++i) {
////            r = new CreateRequest(s);
////            r.setParams(params1);
////            json = JSONValue.toJSONString(r.runHttp());
////            json = JsonPrettyPrinter.prettyPrintJson(json);
////            System.out.println(json);
////        }
////        r = new CreateRequest(s);
////        r.setParams(params2);
////        json = JSONValue.toJSONString(r.runHttp());
////        json = JsonPrettyPrinter.prettyPrintJson(json);
////        System.out.println(json);
////        r = new CreateRequest(s);
////        r.setParams(params3);
////        json = JSONValue.toJSONString(r.runHttp());
////        json = JsonPrettyPrinter.prettyPrintJson(json);
////        System.out.println(json);
////        s.waitUntilQueueEmpty();
////        System.out.println(s.readEntity(new IdDetail(new Muid(
////                "ce0058ac39a33616"), Detail.PROFILE)));
////        r = new CreateRequest(s);
////        r.setParams(params4);
////        json = JSONValue.toJSONString(r.runHttp());
////        json = JsonPrettyPrinter.prettyPrintJson(json);
////        System.out.println(json);
////        s.waitUntilQueueEmpty();
////        System.out.println(s.readEntity(new IdDetail(new Muid(
////                "ce0058ac39a33616"), Detail.PROFILE)));
//        
//        long t = System.currentTimeMillis();
//        
//        for (int i = 0; i != 1000*1000; ++i) {
//            Map<String, String[]> params = new HashMap<String, String[]>();
//            params.put("id",        new String[]{i+""});
//            params.put("type",      new String[]{"Musician"});
//            params.put("name",      new String[]{"Musician " + i});
//            params.put("url",       new String[]{"/musician/Musician" + i});
//            params.put("active",    new String[]{"true"});
//            params.put("founder",   new String[]{"true"});
//            params.put("spans",     new String[]{"1989-now"});
//            Request r = new CreateRequest(s);
//            r.setParams(params);
//            r.runHttp();
////            String json = JSONValue.toJSONString(r.runHttp());
////            json = JsonPrettyPrinter.prettyPrintJson(json);
////            System.out.println(json);
//            if (i % 1000 == 0)
//                System.out.println(((float) i) / (1000.0*1000.0) + "%");
//        }
//        
//        s.waitUntilQueueEmpty();
//        
//        long tt = System.currentTimeMillis();
//        
//        System.out.println(tt - t + "ms");
//        
//        s.stop();
//    }
    
}
