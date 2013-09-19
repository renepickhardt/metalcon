package de.metalcon.sdd.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sdd.request.UpdateRequest;

public class Update extends Servlet {

    private static final long serialVersionUID = 2723043032618956203L;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        UpdateRequest r = new UpdateRequest(server);
        r.setParams(request.getParameterMap());
        JsonResponder.writeJsonResponse(response, r.runHttp());
    }

}
