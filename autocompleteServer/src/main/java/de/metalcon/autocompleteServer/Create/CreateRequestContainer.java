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
	private String suggestString;
	private Integer weight;
	private String key;
	private String imageBase64;
	private String indexName;
	private CreateServlet servlet;

	public String getSuggestString() {
		return this.suggestString;
	}

	public void setSuggestString(String suggestString) {
		this.suggestString = suggestString;
	}

	public Integer getWeight() {
		return this.weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getImageBase64() {
		return this.imageBase64;
	}

	public void setImageBase64(String image) {
		this.imageBase64 = image;
	}

	public String getIndexName() {
		return this.indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	// TODO replace this example suggestTree with concrete ones.
	@Override
	public void run(ServletContext context) {

		SuggestTree suggestTree = (SuggestTree) context
				.getAttribute(ProtocolConstants.INDEX_PARAMETER
						+ this.indexName);

		suggestTree.put(this.suggestString, this.weight, this.key);

		System.out.println(this.suggestString + this.weight + this.key);
		if (this.imageBase64 != null) {
			HashMap<String, String> map = ContextListener
					.getImageIndex(context);
			System.out.println("fetched image index");
			map.put(this.key, this.imageBase64);
			System.out.println(this.key + this.imageBase64);
			ContextListener.setImageIndex(map, context);
		}

		// This creates the database file if it doesn't exist
		File createFile = new File("Database.save");

		try {
			FileOutputStream saveFile = new FileOutputStream("Database.save");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(CreateRequestContainer.this);
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
