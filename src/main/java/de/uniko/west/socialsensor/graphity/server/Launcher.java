package de.uniko.west.socialsensor.graphity.server;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 * server launcher<br>
 * TODO: check if necessary when switching to Tomcat
 * 
 * @author sebschlicht
 * 
 */
public class Launcher {

	/**
	 * database path
	 */
	private static final String DB_PATH = "target/database/location";

	private static void registerShutdownHook(final GraphDatabaseService graphDB) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDB.shutdown();
			}
		});
	}

	public static void main(String[] args) {
		GraphDatabaseService graphDB = new GraphDatabaseFactory()
				.newEmbeddedDatabase(DB_PATH);
		registerShutdownHook(graphDB);

		System.out.println("Tomcat not supported yet!");
	}

}