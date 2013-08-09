package de.uniko.west.socialsensor.graphity.server;

/**
 * server launcher<br>
 * TODO: check if necessary when switching to Tomcat
 * 
 * @author sebschlicht
 * 
 */
public class Launcher {

	/**
	 * database path<br>
	 * TODO: generic
	 */
	private static final String DB_PATH = "target/database/location";

	public static void main(String[] args) {
		final Server server = new Server();
		server.start();
		System.out.println("Tomcat not supported yet!");
		server.stop();
	}

}