package de.metalcon.autocompleteServer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class Server implements ServletContextListener {

	private BlockingQueue<Command> commands;
	private Worker worker;

	public Server() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		this.commands = new LinkedBlockingQueue<Command>();
		this.worker = new Worker(this.commands);
	}

}
