package de.metalcon.autocompleteServer;

import java.io.Serializable;

import javax.servlet.ServletContext;

public abstract class Command implements Serializable {

	public abstract void run(ServletContext context);

	public ServletContext getContext() {
		return this.getContext();
	}
}
