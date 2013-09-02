package de.metalcon.imageServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.metalcon.imageServer.protocol.create.CreateResponse;

public class ServerTest {

	public static void main(String[] args) throws FileNotFoundException {
		ImageStorageServerAPI server = new ImageStorageServer();

		final File testImageFile = new File("/home/sebschlicht/mr_t_ba.png");
		final InputStream imageStream = new FileInputStream(testImageFile);
		final CreateResponse response = new CreateResponse();
		server.createImage("img1", imageStream, "{author:\"Testy\"}", true,
				response);
		System.out.println("image saved!");
	}

}