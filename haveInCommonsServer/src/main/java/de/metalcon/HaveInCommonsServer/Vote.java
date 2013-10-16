package de.metalcon.HaveInCommonsServer;

public enum Vote {
	UP("VOTE.UP") , DOWN("VOTE.DOWN"), NONE("VOTE.NONE");
	
	private String name;
	private Vote(String name){
		this.name = name;
	}
	
	public String getString() {
		return name;
	}
}
