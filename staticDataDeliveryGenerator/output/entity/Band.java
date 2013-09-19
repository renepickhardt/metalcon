package de.metalcon.sdd.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonString;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.server.Server;

public class Band extends Entity {

    String name;

    String url;

    String foundation;

    City city;

    List<Genre> genres;

    List<Musician> musicians;

    List<Event> events;

    List<Tour> tours;

    List<Record> records;

    public Band(Server server) {
        super(server);
    }

    @Override
    public void loadFromJson(String json) {
        Map<String, String> entity = parseJson(json);

        setId(new Muid(entity.get("id")));

        String oid;
        name = entity.get("name");

        url = entity.get("url");

        foundation = entity.get("foundation");

        oid = entity.get("city");
        if (oid == null)
            city = null;
        else {
            city = new City(server);
            city.loadFromId(new Muid(oid));
        }

        genres = new LinkedList<Genre>();
        for (String id : entity.get("genres").split(",")) {
            Genre o = new Genre(server);
            o.loadFromId(new Muid(id));
            genres.add(o);
        }

        musicians = new LinkedList<Musician>();
        for (String id : entity.get("musicians").split(",")) {
            Musician o = new Musician(server);
            o.loadFromId(new Muid(id));
            musicians.add(o);
        }

        events = new LinkedList<Event>();
        for (String id : entity.get("events").split(",")) {
            Event o = new Event(server);
            o.loadFromId(new Muid(id));
            events.add(o);
        }

        tours = new LinkedList<Tour>();
        for (String id : entity.get("tours").split(",")) {
            Tour o = new Tour(server);
            o.loadFromId(new Muid(id));
            tours.add(o);
        }

        records = new LinkedList<Record>();
        for (String id : entity.get("records").split(",")) {
            Record o = new Record(server);
            o.loadFromId(new Muid(id));
            records.add(o);
        }
    }

    @Override
    public void loadFromCreateParams(Map<String, String[]> params) {
        setId(new Muid(getParam(params, "id")));

        String oid;
        name = getParam(params, "name");

        url = getParam(params, "url");

        foundation = getParam(params, "foundation");

        oid = getParam(params, "city", true);
        if (oid == null)
            city = null;
        else {
            city = new City(server);
            city.loadFromId(new Muid(oid));
        }

        genres = new LinkedList<Genre>();
        for (String id : getParam(params, "genres").split(",")) {
            Genre o = new Genre(server);
            o.loadFromId(new Muid(id));
            genres.add(o);
        }

        musicians = new LinkedList<Musician>();
        for (String id : getParam(params, "musicians").split(",")) {
            Musician o = new Musician(server);
            o.loadFromId(new Muid(id));
            musicians.add(o);
        }

        events = new LinkedList<Event>();
        for (String id : getParam(params, "events").split(",")) {
            Event o = new Event(server);
            o.loadFromId(new Muid(id));
            events.add(o);
        }

        tours = new LinkedList<Tour>();
        for (String id : getParam(params, "tours").split(",")) {
            Tour o = new Tour(server);
            o.loadFromId(new Muid(id));
            tours.add(o);
        }

        records = new LinkedList<Record>();
        for (String id : getParam(params, "records").split(",")) {
            Record o = new Record(server);
            o.loadFromId(new Muid(id));
            records.add(o);
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
        List<Muid> ids;
                List<JsonString> os;
        // FULL
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("name", name);
        j.put("url", url);
        j.put("foundation", foundation);
        if (city == null)
            j.put("city", null);
        else
            j.put("city", city.getId().toString());
        ids = new LinkedList<Muid>();
        for (Genre o : genres)
            ids.add(o.getId());
        j.put("genres", joinIds(ids));
        ids = new LinkedList<Muid>();
        for (Musician o : musicians)
            ids.add(o.getId());
        j.put("musicians", joinIds(ids));
        ids = new LinkedList<Muid>();
        for (Event o : events)
            ids.add(o.getId());
        j.put("events", joinIds(ids));
        ids = new LinkedList<Muid>();
        for (Tour o : tours)
            ids.add(o.getId());
        j.put("tours", joinIds(ids));
        ids = new LinkedList<Muid>();
        for (Record o : records)
            ids.add(o.getId());
        j.put("records", joinIds(ids));
        json.put(Detail.FULL, JSONValue.toJSONString(j));

        // SYMBOL
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        j.put("url", url);
        j.put("name", name);
        json.put(Detail.SYMBOL, JSONValue.toJSONString(j));

        // LINE
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        if (city == null)
            j.put("city", null);
        else
            j.put("city", new JsonString(city.getJson(Detail.SYMBOL)));
        j.put("foundation", foundation);
        j.put("url", url);
        j.put("name", name);
        json.put(Detail.LINE, JSONValue.toJSONString(j));

        // PARAGRAPH
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        if (city == null)
            j.put("city", null);
        else
            j.put("city", new JsonString(city.getJson(Detail.LINE)));
        os = new LinkedList<JsonString>();
        for (Record o : records)
            os.add(new JsonString(o.getJson(Detail.LINE)));
        j.put("records", os);
        j.put("foundation", foundation);
        os = new LinkedList<JsonString>();
        for (Genre o : genres)
            os.add(new JsonString(o.getJson(Detail.SYMBOL)));
        j.put("genres", os);
        j.put("url", url);
        j.put("name", name);
        json.put(Detail.PARAGRAPH, JSONValue.toJSONString(j));

        // PROFILE
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        os = new LinkedList<JsonString>();
        for (Event o : events)
            os.add(new JsonString(o.getJson(Detail.LINE)));
        j.put("events", os);
        if (city == null)
            j.put("city", null);
        else
            j.put("city", new JsonString(city.getJson(Detail.LINE)));
        os = new LinkedList<JsonString>();
        for (Record o : records)
            os.add(new JsonString(o.getJson(Detail.LINE)));
        j.put("records", os);
        j.put("foundation", foundation);
        os = new LinkedList<JsonString>();
        for (Musician o : musicians)
            os.add(new JsonString(o.getJson(Detail.PARAGRAPH)));
        j.put("musicians", os);
        os = new LinkedList<JsonString>();
        for (Genre o : genres)
            os.add(new JsonString(o.getJson(Detail.LINE)));
        j.put("genres", os);
        j.put("url", url);
        j.put("name", name);
        os = new LinkedList<JsonString>();
        for (Tour o : tours)
            os.add(new JsonString(o.getJson(Detail.LINE)));
        j.put("tours", os);
        json.put(Detail.PROFILE, JSONValue.toJSONString(j));

        // TOOLTIP
        j = new HashMap<String, Object>();
        j.put("id", getId().toString());
        if (city == null)
            j.put("city", null);
        else
            j.put("city", new JsonString(city.getJson(Detail.SYMBOL)));
        os = new LinkedList<JsonString>();
        for (Record o : records)
            os.add(new JsonString(o.getJson(Detail.SYMBOL)));
        j.put("records", os);
        j.put("foundation", foundation);
        os = new LinkedList<JsonString>();
        for (Genre o : genres)
            os.add(new JsonString(o.getJson(Detail.SYMBOL)));
        j.put("genres", os);
        j.put("url", url);
        j.put("name", name);
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
