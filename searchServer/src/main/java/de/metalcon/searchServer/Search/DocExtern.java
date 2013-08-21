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
    private Map<String, List<String>> highlight;
    
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
    
    public Map<String, List<String>> getHighlight() {
        return highlight;
    }
    
    public void setHighlight(Map<String, List<String>> highlight) {
        this.highlight = highlight;
    }
    
    public Map<String, Object> toJson() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        String title   = this.title;
        String url     = this.url;
        Object content = this.content;
        
        List<String> hlTitle, hlUrl, hlContent;
        hlTitle   = highlight.get("title");
        hlUrl     = highlight.get("url");
        hlContent = highlight.get("content");
        
        if (hlTitle != null)
            title = hlTitle.get(0);
        if (hlUrl != null)
            url = hlUrl.get(0);
        if (hlContent != null)
            content = hlContent;
        
        result.put("title", title);
        result.put("url",   url);
        result.put("text",  content);
        return result;
    }

}
