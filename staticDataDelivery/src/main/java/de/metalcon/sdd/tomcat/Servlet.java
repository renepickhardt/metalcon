package de.metalcon.sdd.tomcat;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.metalcon.sdd.server.Server;

public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 8820626952496106160L;
    
    protected Server server;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        ServletContext context = getServletContext();
        server = (Server) context.getAttribute("server");
    }
    
    public static String getParam(Map<String, String[]> params, String key) {
        String[] vals = params.get(key);
        if (vals != null) {
            String val = vals[0];
            if (val != null)
                return val;
        }
        
        return null;
    }

}
