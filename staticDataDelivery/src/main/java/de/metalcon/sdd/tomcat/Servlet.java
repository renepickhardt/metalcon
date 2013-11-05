package de.metalcon.sdd.tomcat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.metalcon.sdd.Sdd;

public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 8820626952496106160L;
    
    protected Sdd sdd;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        ServletContext context = getServletContext();
        sdd = (Sdd) context.getAttribute("sdd");
    }
    
}
