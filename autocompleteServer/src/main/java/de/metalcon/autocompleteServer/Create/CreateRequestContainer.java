package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletContext;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class CreateRequestContainer extends Command {
	private String suggestString;
	private Integer weight;
	private String key;
	private String imageSerializedString;
	private String indexName;

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

	public String getImageSerializedString() {
		return this.imageSerializedString;
	}

	public void setImageSerializedString(String imageSerializedString) {
		this.imageSerializedString = imageSerializedString;
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
				.getAttribute("indexName:" + this.indexName);

		suggestTree.put(this.suggestString, this.weight, this.key);

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

	}

}
