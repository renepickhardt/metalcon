package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class CreateRequestContainer extends Command {

	private SuggestionComponents suggestionComponents;
	private CreateServlet servlet;

	public SuggestionComponents getComponents() {
		return this.suggestionComponents;
	}

	@Override
	public void run(ServletContext context) {

		SuggestTree suggestTree = (SuggestTree) context
				.getAttribute(ProtocolConstants.INDEX_PARAMETER
						+ this.suggestionComponents.getIndexName());

		suggestTree.put(this.suggestionComponents.getSuggestString(),
				this.suggestionComponents.getWeight(),
				this.suggestionComponents.getKey());

		// System.out.println(this.suggestionComponents.getSuggestString() +
		// this.suggestionComponents.getWeight().toString() +
		// this.suggestionComponents.getKey());
		if (this.suggestionComponents.getImageBase64() != null) {
			HashMap<String, String> map = ContextListener
					.getImageIndex(context);
			// System.out.println("fetched image index");
			map.put(this.suggestionComponents.getKey(),
					this.suggestionComponents.getImageBase64());
			// System.out.println(this.suggestionComponents.getKey() +
			// this.suggestionComponents.getImageBase64());
			ContextListener.setImageIndex(map, context);
		}

		// This creates the database file if it doesn't exist
		File createFile = new File("Database.save");

		try {
			FileOutputStream saveFile = new FileOutputStream("Database.save");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(this.suggestionComponents);
			save.close();

		}
		// maybe there is a way to store failed save-processes, to try them
		// again, when the issue is solved?^
		catch (IOException e) {
			e.printStackTrace();
		}

		this.servlet.commandFinished();

	}

	public CreateServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final CreateServlet servlet) {
		this.servlet = servlet;
	}

}
