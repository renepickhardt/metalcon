package de.metalcon.searchServer.Search;

import org.apache.solr.client.solrj.beans.Field;

public class DocExtern {

    private String title;
    private String url;
    
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

}
