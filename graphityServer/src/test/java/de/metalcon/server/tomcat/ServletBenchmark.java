package de.metalcon.server.tomcat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import de.metalcon.server.tomcat.create.CreateType;
import de.metalcon.server.tomcat.delete.DeleteType;

/**
 * simple benchmark for all CRUD requests in NSSP the server can handle
 * 
 * @author sebschlicht
 * 
 */
public class ServletBenchmark {

	public static void main(final String[] args) throws IOException {
		final FileReader fileReader = new FileReader(
				"/home/sebschlicht/de-events.log");
		final BufferedReader reader = new BufferedReader(fileReader);
		final HashMap<String, Boolean> userExists = new HashMap<String, Boolean>();

		String line;
		long event = 0;
		long statusUpdateId = 0;
		long time = System.nanoTime();

		while ((line = reader.readLine()) != null) {
			final String[] values = line.split("\t");

			if (values.length == 3) {
				if (!userExists.containsKey(values[2])) {
					createUserRequest(values[2]);
				}

				// create status update
				createStatusUpdateRequest(values[2],
						String.valueOf(statusUpdateId));
				statusUpdateId += 1;
			} else {
				if (!userExists.containsKey(values[2])) {
					createUserRequest(values[2]);
				}
				if (!userExists.containsKey(values[3])) {
					createUserRequest(values[3]);
				}

				if ("A".equals(values[1])) {
					// create follow edge
					createFollowEdge(values[2], values[3]);
				} else {
					// delete follow edge
					deleteFollowEdge(values[2], values[3]);
				}
			}

			event += 1;
			if ((event % 1000) == 0) {
				System.out.println("1000 events in "
						+ ((System.nanoTime() - time) / 1000)
						+ " macro seconds");
				time = System.nanoTime();
			}
		}

		reader.close();
	}

	private static InputStream createUserRequest(final String userId)
			throws ClientProtocolException, IOException {
		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(NSSProtocol.Create.TYPE, new StringBody(
					CreateType.USER.getIdentifier()));
			entity.addPart(NSSProtocol.Create.USER_ID, new StringBody(userId));
			entity.addPart(NSSProtocol.Create.USER_DISPLAY_NAME,
					new StringBody("user" + userId));
			entity.addPart(NSSProtocol.Create.USER_PROFILE_PICTURE_PATH,
					new StringBody("http://google.de/somepic"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return postRequest(
				"http://localhost:8080/Graphity-Server-0.0.1-SNAPSHOT/create",
				entity);
	}

	private static InputStream createStatusUpdateRequest(final String userId,
			final String statusUpdateId) throws ClientProtocolException,
			IOException {
		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(NSSProtocol.USER_ID, new StringBody(userId));
			entity.addPart(NSSProtocol.Create.TYPE, new StringBody(
					CreateType.STATUS_UPDATE.getIdentifier()));
			entity.addPart(NSSProtocol.Create.STATUS_UPDATE_ID, new StringBody(
					statusUpdateId));
			entity.addPart(NSSProtocol.Create.STATUS_UPDATE_TYPE,
					new StringBody("Plain"));
			entity.addPart(
					"message",
					new StringBody(
							"myDefaultStatusUpdateTextMessageDoesContainsAsMuchCharactersAsIWant!"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return postRequest(
				"http://localhost:8080/Graphity-Server-0.0.1-SNAPSHOT/create",
				entity);
	}

	private static InputStream createFollowEdge(final String followingId,
			final String followedId) throws ClientProtocolException,
			IOException {
		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(NSSProtocol.USER_ID, new StringBody(followingId));
			entity.addPart(NSSProtocol.Create.TYPE, new StringBody(
					CreateType.FOLLOW.getIdentifier()));
			entity.addPart(NSSProtocol.Create.FOLLOW_TARGET, new StringBody(
					followedId));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return postRequest(
				"http://localhost:8080/Graphity-Server-0.0.1-SNAPSHOT/create",
				entity);
	}

	private static InputStream deleteFollowEdge(final String followingId,
			final String followedId) throws ClientProtocolException,
			IOException {
		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(NSSProtocol.USER_ID, new StringBody(followingId));
			entity.addPart(NSSProtocol.Delete.TYPE, new StringBody(
					DeleteType.FOLLOW.getIdentifier()));
			entity.addPart(NSSProtocol.Delete.FOLLOWED, new StringBody(
					followedId));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return postRequest(
				"http://localhost:8080/Graphity-Server-0.0.1-SNAPSHOT/delete",
				entity);
	}

	private static InputStream postRequest(final String url,
			final HttpEntity entity) throws ClientProtocolException,
			IOException {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);

		final HttpResponse httpResponse = httpClient.execute(httpPost);
		final HttpEntity responseEntity = httpResponse.getEntity();
		return responseEntity.getContent();
	}

}