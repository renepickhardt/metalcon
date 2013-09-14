package de.metalcon.sdd.tomcat;

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

}
