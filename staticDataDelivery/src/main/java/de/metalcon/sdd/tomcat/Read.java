package de.metalcon.sdd.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sdd.request.ReadRequest;

public class Read extends Servlet {

    private static final long serialVersionUID = -3979596556752428300L;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        ReadRequest r = new ReadRequest(server);
        r.setQuery(request.getQueryString());
        JsonResponder.writeJsonResponse(response, r.run());
    }

}
