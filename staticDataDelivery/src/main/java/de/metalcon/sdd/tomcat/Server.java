package de.metalcon.sdd.tomcat;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.metalcon.sdd.Sdd;
import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.TempConfig;
import de.metalcon.sdd.error.InvalidConfigException;

public class Server implements ServletContextListener {

    private Sdd sdd;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Config config = new TempConfig();
            sdd = new Sdd(config);
        } catch (IOException | InvalidConfigException e) {
            // TODO: log
            e.printStackTrace();
        }
        
        ServletContext context = sce.getServletContext();
        context.setAttribute("sdd", sdd);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            sdd.close();
        } catch (IOException e) {
            // TODO: log
            e.printStackTrace();
        }
    }

}
