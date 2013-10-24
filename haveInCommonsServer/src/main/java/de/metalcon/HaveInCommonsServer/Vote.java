package de.metalcon.HaveInCommonsServer;

public enum Vote {
	UP("VOTE.UP") , DOWN("VOTE.DOWN"), NONE("VOTE.NEUTRAL");
	
	private String name;
	private Vote(String name){
		this.name = name;
	}
	
	public String getString() {
		return name;
	}
}
