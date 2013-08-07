package de.metalcon.autocompleteServer;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import de.metalcon.autocompleteServer.Search;

public class ContextListener implements ServletContextListener {

	public static SuggestTree getIndex(String indexName, ServletContext context){
		return (SuggestTree)context.getAttribute("indexName:"+indexName);
	}
	
	public void contextDestroyed(ServletContextEvent arg0){
		//code to be run on server shutdown goes here
	}

	public void contextInitialized(ServletContextEvent arg0){
		Search.initilizeSuggestTree();
	}
}
