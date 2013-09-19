package de.metalcon.sdd.entity;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.server.Server;

public class Musician extends Entity {

    String name;

    String url;

    String active;

    String founder;

    String spans;

    Band band;

    public Musician(Server server) {
        super(server);
    }

    @Override
    public void loadFromJson(String json) {
        Map<String, String> entity = parseJson(json);

        setId(new Muid(entity.get("id")));

        String oid;
        name = entity.get("name");

        url = entity.get("url");

        active = entity.get("active");

        founder = entity.get("founder");

        spans = entity.get("spans");

        oid = entity.get("band");
        if (oid == null)
            band = null;
        else {
            band = new Band(server);
            band.loadFromId(new Muid(oid));
        }
    }

    @Override
    public void loadFromCreateParams(Map<String, String[]> params) {
        setId(new Muid(getParam(params, "id")));

        String oid;
        name = getParam(params, "name");

        url = getParam(params, "url");

        active = getParam(params, "active");

        founder = getParam(params, "founder");

        spans = getParam(params, "spans");

        oid = getParam(params, "band", true);
        if (oid == null)
            band = null;
        else {
            band = new Band(server);
            band.loadFromId(new Muid(oid));
        }
    }

    @Override
    public void loadFromUpdateParams(Map<String, String[]> params) {
        Muid id = new Muid(getParam(params, "id"));
        loadFromId(id);

        //<LOAD_FROM_UPDATE_PARAMS>
    }

    @Override
    protected void generateJson() {
        Map<String, Object> j;
        // FULL
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        j.put("url", url);
        j.put("active", active);
        j.put("founder", founder);
        j.put("spans", spans);
        if (band == null)
            j.put("band", null);
        else
            j.put("band", band.getId().toString());
        json.put(Detail.FULL, JSONValue.toJSONString(j));

        // SYMBOL
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        j.put("url", url);
        json.put(Detail.SYMBOL, JSONValue.toJSONString(j));

        // LINE
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        if (band == null)
            j.put("band", null);
        else
            j.put("band", new JsonString(band.getJson(Detail.SYMBOL)));
        j.put("url", url);
        json.put(Detail.LINE, JSONValue.toJSONString(j));

        // PARAGRAPH
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.PARAGRAPH, JSONValue.toJSONString(j));

        // PROFILE
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.PROFILE, JSONValue.toJSONString(j));

        // TOOLTIP
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.TOOLTIP, JSONValue.toJSONString(j));

        // SEARCH_ENTRY
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.SEARCH_ENTRY, JSONValue.toJSONString(j));

        // SEARCH_DETAILED
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        json.put(Detail.SEARCH_DETAILED, JSONValue.toJSONString(j));
    }

}
