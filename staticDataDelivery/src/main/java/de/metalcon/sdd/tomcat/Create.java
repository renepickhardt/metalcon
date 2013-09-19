package de.metalcon.sdd.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sdd.request.CreateRequest;

public class Create extends Servlet {

    private static final long serialVersionUID = -6942160975188033156L;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        CreateRequest r = new CreateRequest(server);
        r.setParams(request.getParameterMap());
        JsonResponder.writeJsonResponse(response, r.runHttp());
    }

}
