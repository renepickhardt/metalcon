package de.metalcon.middleware.core.request;

import org.springframework.web.client.RestTemplate;

public class JsonRequest implements Request {

    static private RestTemplate restTemplate;
    static {
        restTemplate = new RestTemplate();
    }

    private String url;

    public JsonRequest(
            String url) {
        this.url = url;
    }

    @Override
    public Object run() {
        return restTemplate.getForObject(url, String.class);
    }

}
