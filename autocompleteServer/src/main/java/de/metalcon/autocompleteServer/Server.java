package de.metalcon.autocompleteServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Server implements ServletContextListener {

	private final BlockingQueue<Command> commands;
	private final Worker worker;

	public Server() {
		this.commands = new LinkedBlockingQueue<Command>();
		this.worker = new Worker(this.commands);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		this.worker.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		final ServletContext context = arg0.getServletContext();
		context.setAttribute("queue", this.commands);
		Search.initilizeSuggestTree(context);
	}

}
