package de.metalcon.autocompleteServer;

import javax.servlet.ServletContext;

public abstract class Command {

	public abstract void run(ServletContext context);

	public ServletContext getContext() {
		return this.getContext();
	}
}
