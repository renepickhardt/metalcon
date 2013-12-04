package de.metalcon.middleware.util.request;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.web.client.RestTemplate;

public class RequestTransaction {

    private static RestTemplate restTemplate;
    static {
        restTemplate = new RestTemplate();
    }

    private Queue<Request>      requests;

    public RequestTransaction() {
        requests = new LinkedList<Request>();
    }

    public void request(Request request) {
        requests.add(request);
    }

    public String recieve() {
        if (requests.isEmpty())
            return null;

        Request request = requests.poll();

        String result;
        switch (request.getRequestMethod()) {
            case GET:
                result = restTemplate.getForObject(request.getUrl(),
                                                   String.class);
                break;

            default:
                throw new UnsupportedOperationException();
        }
        return result;
    }

}
