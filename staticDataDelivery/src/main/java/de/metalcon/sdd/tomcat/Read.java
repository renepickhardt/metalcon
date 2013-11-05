package de.metalcon.sdd.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Read extends Servlet {

    private static final long serialVersionUID = 7060493709035083463L;
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
    throws IOException {
        JsonResponder.writeJsonResponse(response, "\"death in fire!\"");
    }

}
