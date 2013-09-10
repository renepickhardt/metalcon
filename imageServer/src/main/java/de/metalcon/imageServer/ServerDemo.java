package de.metalcon.imageServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.metalcon.imageServer.protocol.create.CreateResponse;

public class ServerDemo {

	public static void main(String[] args) throws FileNotFoundException {
		final ImageStorageServer server = new ImageStorageServer("iss.config");
		server.clear();

		if (server.isRunning()) {
			System.out.println("image server running.");

			final CreateResponse response = new CreateResponse();
			final String id1 = "img1";
			final InputStream in1 = new FileInputStream(
					"/home/sebschlicht/mr_t_ba.png");
			final String metaData = "{\"author\":\"Testy\"}";
			if (!server.createImage(id1, in1, metaData, 100, 100, 200, 200,
					response)) {
				System.out.println("creation failed!");
			}
		}
	}

}