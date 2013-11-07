package de.metalcon.sdd.tomcat;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.config.Config;

public abstract class Servlet extends HttpServlet {

    private static final long serialVersionUID = 8820626952496106160L;
    
    protected Config config;
    
    protected Sdd sdd;
    
    protected abstract String run(HttpServletRequest request)
    throws Exception;
    
    @Override
    protected void doGet(HttpServletRequest  request,
                         HttpServletResponse response)
    throws IOException {
        try {
            JsonResponder.writeJsonResponse(response, run(request));
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            JsonResponder.writeJsonResponse(response, stackTrace.toString());
        }
    }
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        
        ServletContext context = getServletContext();
        config = (Config) context.getAttribute("sdd-config");
        sdd    = (Sdd)    context.getAttribute("sdd");
    }
    
}
