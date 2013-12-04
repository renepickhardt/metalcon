package de.metalcon.middleware.util.request;

public class RequestTransaction {

    private RequestManager requestManager;
    
    /* package */ RequestTransaction(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    public void request(Request request) {
        requestManager.request(this, request);
    }

    public Object recieve() {
        return requestManager.recieve(this);
    }

}
