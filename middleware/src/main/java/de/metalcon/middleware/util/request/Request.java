package de.metalcon.middleware.util.request;

import org.springframework.web.bind.annotation.RequestMethod;

public interface Request {

    String getUrl();

    RequestMethod getRequestMethod();

}
