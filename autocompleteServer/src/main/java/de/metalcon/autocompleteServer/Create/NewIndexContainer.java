package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class NewIndexContainer extends Command implements Serializable {

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

		// This creates the database file
		// FIXME: use config-parameter for file-path
		File newFile = new File("/var/lib/tomcat/" + this.indexName + ".save");
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// this.createFileOnDisc(newFile);

		this.servlet.commandFinished();

	}

	public NewIndexServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final NewIndexServlet servlet) {
		this.servlet = servlet;
	}
}
