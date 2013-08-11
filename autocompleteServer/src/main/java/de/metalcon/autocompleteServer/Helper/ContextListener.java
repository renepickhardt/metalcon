package de.metalcon.autocompleteServer.Helper;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import de.metalcon.autocompleteServer.Search;

public class ContextListener implements ServletContextListener {

	//TODO: make this cleaner and nicer. having this constant prefix here calls for trouble
	public static SuggestTree getIndex(String indexName, ServletContext context){
		return (SuggestTree)context.getAttribute("indexName:"+indexName);
	}
	public static void setIndex(String indexName, SuggestTree index, ServletContext context){
		context.setAttribute("indexName:" + indexName, index);
	}
	
	public static HashMap<String, String> getImageIndex(ServletContext context){
		return (HashMap<String, String>)context.getAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY);
	}
	
	public static void setImageIndex(HashMap<String, String> imageIndex, ServletContext context){
		context.setAttribute(ProtocolConstants.IMAGE_SERVER_CONTEXT_KEY, imageIndex);
	}
	
	public void contextDestroyed(ServletContextEvent arg0){
		//code to be run on server shutdown goes here
	}

	public void contextInitialized(ServletContextEvent arg0){
		ServletContext context = arg0.getServletContext();
		Search.initilizeSuggestTree(context);
	}
}
