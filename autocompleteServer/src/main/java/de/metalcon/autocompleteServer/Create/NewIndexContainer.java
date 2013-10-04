package de.metalcon.autocompleteServer.Create;

import java.io.File;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class NewIndexContainer extends Command {

	public NewIndexContainer(final ServletContext context) {
		super(context);
		this.indexName = null;
	}

	private final String indexName;
	private NewIndexServlet servlet;

	public String getIndexName() {

		return this.indexName;
	}

	@Override
	public void run() {
		SuggestTree suggestTree = ContextListener.getIndex(this.getIndexName(),
				this.context);

		// TODO

		// This creates the database file
		File createFile = new File("/var/lib/tomcat/" + this.indexName
				+ ".save");

		this.suggestionComponents.saveToDisc(createFile);

		this.servlet.commandFinished();

	}

	public NewIndexServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final NewIndexServlet servlet) {
		this.servlet = servlet;
	}

}
