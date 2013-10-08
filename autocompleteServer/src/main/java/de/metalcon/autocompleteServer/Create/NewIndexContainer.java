package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class NewIndexContainer extends Command {

	public NewIndexContainer(final ServletContext context,
			final String indexName) {
		super(context);
		this.indexName = indexName;
	}

	private final String indexName;
	private NewIndexServlet servlet;

	public String getIndexName() {

		return this.indexName;
	}

	@Override
	public void run() {
		SuggestTree suggestTree = new SuggestTree(
				ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS);

		ContextListener.setIndex(this.indexName, suggestTree, this.context);

		this.servlet.commandFinished();

	}

	public NewIndexServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final NewIndexServlet servlet) {
		this.servlet = servlet;
	}
}
