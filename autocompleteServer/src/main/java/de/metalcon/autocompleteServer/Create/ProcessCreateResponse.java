package de.metalcon.autocompleteServer.Create;

public class ProcessCreateResponse {

	private final CreateResponseContainer container;

	public ProcessCreateResponse() {
		this.container = new CreateResponseContainer();
	}

	public void addQueryNameWarning(String querynameNotGiven) {
		this.container.addStatusMessage(querynameNotGiven);

	}

	public void addIndexNameWarning(String indexnameNotGiven) {
		this.container.addStatusMessage(indexnameNotGiven);
	}

	public void addError(String querynameNotGiven) {
		this.container.addStatusMessage(querynameNotGiven);
	}

	/**
	 * Adds the suggestion String to the container object. Expects the String to
	 * be not NULL and correctly formatted.
	 * 
	 * @param suggestionString
	 */
	public void addSuggestStringToContainer(String suggestionString) {
		this.container.setSuggestString(suggestionString);
	}

	public void addSuggestionKeyWarning(String suggestionKeyNotGiven) {
		this.container.addStatusMessage(suggestionKeyNotGiven);
	}

	public void addDefaultIndexWarning(String indexnameNotGiven) {
		this.container.addStatusMessage(indexnameNotGiven);
	}

	public void addNoImageWarning(String noImage) {
		this.container.addStatusMessage(noImage);
	}

	public String getResponse() {
		return this.container.getStatusMessages();
	}
	/*
	 * public void addIndexToContainer(String indexName) {
	 * this.container.setIndexName(indexName); }
	 * 
	 * public void addSuggestionKeyToContainer(String suggestionKey) {
	 * this.container.setKey(suggestionKey); }
	 * 
	 * public void addWeightToContainer(Integer weight) {
	 * this.container.setWeight(weight);
	 * 
	 * }
	 */
}
