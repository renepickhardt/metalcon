package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
		File newFile = new File("/var/lib/tomcat/" + this.indexName + ".save");

		this.createFileOnDisc(newFile);

		this.servlet.commandFinished();

	}

	public NewIndexServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final NewIndexServlet servlet) {
		this.servlet = servlet;
	}

	public void createFileOnDisc(File newFile) {
		try {

			// advice found here:
			// http://stackoverflow.com/questions/1194656/appending-to-an-objectoutputstream/1195078#1195078
			if (!(newFile.exists())) {
				FileOutputStream saveFile = new FileOutputStream(newFile, false);

				ObjectOutputStream save = new ObjectOutputStream(saveFile);
				save.writeObject(this);
				save.close();
				saveFile.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
