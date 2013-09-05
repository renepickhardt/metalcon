package de.metalcon.imageServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import de.metalcon.imageServer.protocol.create.CreateResponse;
import de.metalcon.imageServer.protocol.read.ReadResponse;

public class ServerTest {

	public static void main(String[] args) throws IOException {
		ImageStorageServerAPI server = new ImageStorageServer();

		final File testImageFile = new File("/home/sebschlicht/mr_t_ba.png");
		final InputStream imageStream = new FileInputStream(testImageFile);
		final InputStream imageStream2 = new FileInputStream(testImageFile);
		final CreateResponse createResponse = new CreateResponse();
		if (!server.createImage("img1", imageStream, "", false, createResponse)) {
			System.out.println("failed to create image 1!");
		}
		if (!server.createImage("img2", imageStream2, "{author:\"Testy\"}",
				100, 100, 200, 200, createResponse)) {
			System.out.println("failed to create image 2!");
		}

		final ReadResponse readResponse = new ReadResponse();
		final ImageData img1 = server.readImageWithMetaData("img1",
				readResponse);
		if ((img1 == null) || (img1.getImageStream() == null)) {
			System.out.println("failed to read original image img1");
		}
		final InputStream img2 = server.readImage("img2", 100, 100,
				readResponse);
		if (img2 == null) {
			System.out.println("failed to read scaled image img2");
		} else {
			FileOutputStream out = new FileOutputStream(
					"/etc/imageStorageServer/100x100.jpg");
			IOUtils.copy(img2, out);
		}
	}

}
