package de.metalcon.autocompleteServer.Create;

import de.metalcon.autocompleteServer.Command;
import de.metalcon.autocompleteServer.Search;

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
	public void run() {
		Search.suggestTree.put(this.suggestString, this.weight, this.key);
	}

}
