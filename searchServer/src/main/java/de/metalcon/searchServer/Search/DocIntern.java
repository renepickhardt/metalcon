package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

public class DocIntern implements Doc {

    private String id = "";
    private String facet = "";
    private String name = "";
    private String image = "";
    private String text = "";
    private String display = "";
    
    public String getId() {
        return id;
    }

    @Field("id")
    public void setId(String id) {
        this.id = id;
    }

    public String getFacet() {
        return facet;
    }

    @Field("facet")
    public void setFacet(String facet) {
        this.facet = facet;
    }

    public String getName() {
        return name;
    }

    @Field("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    @Field("image")
    public void setImage(String image) {
        this.image = image;
    }
    
    public String getText() {
        return text;
    }
    
    @Field("text")
    public void setText(String text) {
        this.text = text;
    }

    public String getDisplay() {
        return display;
    }

    @Field("display")
    public void setDisplay(String display) {
        this.display = display;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", id);
        result.put("facet", facet);
        result.put("name", name);
        result.put("image", image);
        result.put("display", display);
        return result;
    }

}
