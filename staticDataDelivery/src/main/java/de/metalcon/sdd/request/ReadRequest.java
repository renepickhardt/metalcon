package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.JsonString;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.error.ReadRequestNoQuerySddError;
import de.metalcon.sdd.server.Server;

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
        r.setQuery("ec94771fed93cffc:all,664421a1b59263c5:all");
        String json = JSONValue.toJSONString(r.runHttp());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        
        s.stop();
    }

}
