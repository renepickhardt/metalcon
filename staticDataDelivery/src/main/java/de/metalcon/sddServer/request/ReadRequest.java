package de.metalcon.sddServer.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.JsonString;
import de.metalcon.sddServer.IdDetail;
import de.metalcon.sddServer.error.ReadRequestNoQuerySddError;
import de.metalcon.sddServer.server.Server;

public class ReadRequest extends Request {
    
    final public static char queryDelimeter = ',';

    private String query;
    
    public ReadRequest(Server server) {
        super(server);
    }

    public void setQuery(String query) {
        if (query == null)
            throw new IllegalArgumentException("query was null");
        
        this.query = query;
    }

    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();

        if (query == null)
            throw new ReadRequestNoQuerySddError();
        
        for (String q : query.split(String.valueOf(queryDelimeter))) {
            IdDetail idDetail = new IdDetail(server, q);
            result.put(q, new JsonString(server.readEntityJson(idDetail)));
        }

        return result;
    }

    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start(); 
        
        ReadRequest r = new ReadRequest(s);
//        r.setQuery("2f364c13c0114e16:line,11233033e2b36cff:line,ce0058ac39a33616:profile");
//        r.setQuery("band3203:all,band714:all");
//        r.setQuery("band_01:all,record_01:all,record_02:all");
//        r.setQuery("record16936:all,band3203:all,band3006:all,record19960:all");
//        r.setQuery("record16936:title,band3203:title,band3006:title,record19960:title");
        r.setQuery("record19960:all");
        long t = System.currentTimeMillis();
        String json = JSONValue.toJSONString(r.runHttp());
        System.out.println(System.currentTimeMillis() - t);
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        r = new ReadRequest(s);
        r.setQuery("band3203:all");
        t = System.currentTimeMillis();
        json = JSONValue.toJSONString(r.runHttp());
        System.out.println(System.currentTimeMillis() - t);
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        
        s.stop();
    }

}
