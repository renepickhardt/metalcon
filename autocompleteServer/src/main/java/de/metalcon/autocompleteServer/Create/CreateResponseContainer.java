package de.metalcon.autocompleteServer.Create;

public class CreateResponseContainer {

	private String suggestString;
	private StringBuffer statusMessage;

	public void setSuggestString(String suggestString) {
		this.suggestString = suggestString;
	}

	public String getSuggestString() {
		return this.suggestString;
	}

	public void addStatusMessage(String message) {
		this.statusMessage.append(message);
	}

	public String getStatusMessages() {
		return this.statusMessage.toString();
	}
}
