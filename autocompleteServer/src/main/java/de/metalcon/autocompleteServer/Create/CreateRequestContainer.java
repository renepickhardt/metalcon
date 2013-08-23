package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.ContextListener;
import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class CreateRequestContainer extends Command {

	public CreateRequestContainer(final ServletContext context) {
		super(context);
		this.suggestionComponents = new SuggestionComponents();
	}

	private final SuggestionComponents suggestionComponents;
	private CreateServlet servlet;

	public SuggestionComponents getComponents() {

		return this.suggestionComponents;
	}

	@Override
	public void run() {

		SuggestTree suggestTree = (SuggestTree) this.context
				.getAttribute(ProtocolConstants.INDEX_PARAMETER
						+ this.suggestionComponents.getIndexName());

		System.out.println(this.suggestionComponents);

		suggestTree.put(this.suggestionComponents.getSuggestString(),
				this.suggestionComponents.getWeight(),
				this.suggestionComponents.getKey());

		if (this.suggestionComponents.getImageBase64() != null) {
			HashMap<String, String> map = ContextListener
					.getImageIndex(this.context);
			map.put(this.suggestionComponents.getKey(),
					this.suggestionComponents.getImageBase64());
			ContextListener.setImageIndex(map, this.context);
		}

		// This creates the database file if it doesn't exist
		File createFile = new File("Database.save");

		this.suggestionComponents.saveToDisc(createFile);

		this.servlet.commandFinished();

	}

	public CreateServlet getRequestServlet() {
		return this.servlet;
	}

	public void setRequestServlet(final CreateServlet servlet) {
		this.servlet = servlet;
	}

}
