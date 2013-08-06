package de.metalcon.autocompleteServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import de.metalcon.autocompleteServer.Search;

public class ContextListener implements ServletContextListener {

	
	public void contextDestroyed(ServletContextEvent arg0){
		//code to be run on server shutdown goes here
	}

	public void contextInitialized(ServletContextEvent arg0){
		Search.initilizeSuggestTree();
	}
}
