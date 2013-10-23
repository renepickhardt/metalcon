package de.metalcon.sddServer.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sddServer.request.ReadRequest;


public class Benchmark extends Servlet {

    private static final long serialVersionUID = -5137436595475512103L;
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        if (request.getParameter("bench") != null) {
            ReadRequest r = null;
            switch (request.getParameter("bench")) {
                case "rdmread01":
                    long t = System.nanoTime();
                    r = new ReadRequest(server);
                    r.setQuery(server.getRandomEntityMuid().toString() + ":" +
                               ((Math.random() + 0.5 > 1) ? "all" : "title"));
                    JsonResponder.writeJsonResponse(response, r.runHttp());
                    long tt = System.nanoTime() - t;
                    System.out.println(tt);
                    break;
                    
                case "rdmread02":
                    r = new ReadRequest(server);
                    r.setQuery(server.getRandomEntityMuid().toString() + ":" +
                               ((Math.random() + 0.5 > 1) ? "all" : "title"));
                    JsonResponder.writeJsonResponse(response, "\"worked.\"");
                    break;
    
                default:
                    JsonResponder.writeJsonResponse(response,
                            "\"Unkown or missing parameter 'bench'.\"");
                    break;
            }
        }
    }

}
