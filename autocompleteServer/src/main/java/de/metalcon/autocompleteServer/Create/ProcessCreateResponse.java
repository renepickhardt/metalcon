package de.metalcon.autocompleteServer.Create;

public class ProcessCreateResponse {

	private final CreateRequestContainer container;

	public ProcessCreateResponse() {
		this.container = new CreateRequestContainer();
	}

	public static void addQueryNameWarning(String querynameNotGiven) {
		// TODO implement appropriate warning

	}

	public static void addIndexNameWarning(String indexnameNotGiven) {
		// TODO implement appropriate warning

	}

	public void addError(String querynameNotGiven) {
		// TODO implement appropriate error message

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
		// TODO implement appropriate warning

	}

	public void addDefaultIndexWarning(String indexnameNotGiven) {
		// TODO implement appropriate warning

	}

	public void addNoImageWarning(String noImage) {
		// TODO implement appropriate warning

	}

}
