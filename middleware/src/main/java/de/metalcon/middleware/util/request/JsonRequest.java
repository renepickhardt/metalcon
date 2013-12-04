package de.metalcon.middleware.util.request;

import org.springframework.web.bind.annotation.RequestMethod;

public class JsonRequest implements Request {
    
    private String url;

    public JsonRequest(String url) {
        this.url = url;
    }
    
    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

}
