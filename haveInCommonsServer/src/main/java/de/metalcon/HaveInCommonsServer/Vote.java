package de.metalcon.HaveInCommonsServer;

public enum Vote {
	UP("VOTE.UP") , DOWN("VOTE.DOWN"), NEUTRAL("VOTE.NEUTRAL");
	
	private String name;
	private Vote(String name){
		this.name = name;
	}
	
	public String getString() {
		return name;
	}
}
