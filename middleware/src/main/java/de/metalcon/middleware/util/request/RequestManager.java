package de.metalcon.middleware.util.request;

public class RequestManager {

    public RequestTransaction startTransaction() {
        return new RequestTransaction();
    }

}
