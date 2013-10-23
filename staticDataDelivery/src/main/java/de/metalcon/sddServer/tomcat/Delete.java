package de.metalcon.sddServer.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sddServer.request.DeleteRequest;

public class Delete extends Servlet {

    private static final long serialVersionUID = 6386629842911312800L;

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        DeleteRequest r = new DeleteRequest(server);
        r.setParams(request.getParameterMap());
        JsonResponder.writeJsonResponse(response, r.runHttp());
    }

}
