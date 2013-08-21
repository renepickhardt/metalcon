package de.metalcon.searchServer.Search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

public class DocExtern {

    private String id = "";
    private String title = "";
    private String url = "";
    private String content = "";
    public List<String> highlight = null;
    
    public String getId() {
        return id;
    }
    
    @Field("id")
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    @Field("title")
    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    @Field("url")
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getContent() {
        return content;
    }
    
    @Field("content")
    public void setContent(String content) {
        this.content = content;
    }
    
    public Map<String, Object> toJson() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("title",   title);
        result.put("url",     url);
        result.put("content", content);
        result.put("highlight", highlight);
        return result;
    }

}
