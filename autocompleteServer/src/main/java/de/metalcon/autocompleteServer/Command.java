package de.metalcon.autocompleteServer;

import javax.servlet.ServletContext;

public abstract class Command {

	protected final ServletContext context;

	public Command(ServletContext context) {
		this.context = context;
	}

	public abstract void run();

	public ServletContext getContext() {
		return this.context;
	}
}
