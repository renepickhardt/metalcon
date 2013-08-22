package de.metalcon.autocompleteServer.Create;

import java.io.Serializable;

public class SuggestionComponents implements Serializable {

	private static final long serialVersionUID = 1L;
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
}
