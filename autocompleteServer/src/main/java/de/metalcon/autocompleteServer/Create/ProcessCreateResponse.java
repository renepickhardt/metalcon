package de.metalcon.autocompleteServer.Create;

public class ProcessCreateResponse {

	private final CreateRequestContainer container;

	public ProcessCreateResponse() {
		this.container = new CreateRequestContainer();
	}

	public static void addQueryNameWarning(String querynameNotGiven) {
		// TODO Auto-generated method stub

	}

	public static void addIndexNameWarning(String indexnameNotGiven) {
		// TODO Auto-generated method stub

	}

	public void addError(String querynameNotGiven) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	public void addDefaultIndexWarning(String indexnameNotGiven) {
		// TODO Auto-generated method stub

	}

}
