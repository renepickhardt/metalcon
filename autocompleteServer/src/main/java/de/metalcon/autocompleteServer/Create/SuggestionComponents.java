package de.metalcon.autocompleteServer.Create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 
 * Contains the data extracted from the HTTP-request, which will be inserted
 * into the suggestTree and stored to disc.
 * 
 * @author Christian Schowalter
 * 
 */
public class SuggestionComponents implements Serializable {

	private static final long serialVersionUID = 7311975384159541028L;

	private String suggestString;
	private Integer weight;
	private String key;
	private String imageBase64;
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

	public void saveToDisc(File createFile) {
		try {
			FileOutputStream saveFile = new FileOutputStream(createFile, true);

			// TODO check if AppendableObjectOutputStream has to be used instead
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(this);
			save.close();

		}
		// maybe there is a way to store failed save-processes, to try them
		// again, when the issue is solved?^
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
