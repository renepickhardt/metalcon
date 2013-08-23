package de.metalcon.server.tomcat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.create.CreateRequestType;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequestType;

/**
 * simple benchmark for all CRUD requests in NSSP the server can handle
 * 
 * @author sebschlicht
 * 
 */
public class ServletBenchmark {

	private static long totalRead, totalCreateUser, totalCreateFollow,
			totalDeleteFollow, totalCreateStatusUpdate;
	private static long timeRead, timeCreateUser, timeCreateFollow,
			timeDeleteFollow, timeCreateStatusUpdate;
	private static long countRead, countCreateUser, countCreateFollow,
			countDeleteFollow, countCreateStatusUpdate;
	private static long time;

	public static void main(final String[] args) throws IOException {
		final FileReader fileReader = new FileReader(
				"/var/lib/datasets/rawdata/wikievents/simple-events.logSortedByTime");
		final BufferedReader reader = new BufferedReader(fileReader);
		final HashSet<String> userExists = new HashSet<String>();

		String line;
		long event = 0;

		while ((line = reader.readLine()) != null) {
			final String[] values = line.split("\t");

			while ((userExists.size() > 0) && (Math.random() < 0.9)) {
				readStatusUpdateRequest(userExists);
				event += 1;
			}

			if (values.length == 3) {
				if (!userExists.contains(values[2])) {
					createUserRequest(values[2]);
					userExists.add(values[2]);
				}

				// create status update
				createStatusUpdateRequest(values[2],
						String.valueOf(countCreateStatusUpdate));
			} else if (values.length == 4) {
				if (!userExists.contains(values[2])) {
					createUserRequest(values[2]);
					userExists.add(values[2]);
				}
				if (!userExists.contains(values[3])) {
					createUserRequest(values[3]);
					userExists.add(values[3]);
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
			if (event >= 1000) {
				final long timePerEvent = time / 1000 / event;
				final long timePerRead = totalRead / 1000 / countRead;
				final long timePerCreateUser = totalCreateUser / 1000
						/ countCreateUser;
				final long timePerCreateStatusUpdate = totalCreateStatusUpdate
						/ 1000 / countCreateStatusUpdate;
				final long timePerCreateFollow = totalCreateFollow / 1000
						/ countCreateFollow;
				final long timePerDeleteFollow = totalDeleteFollow / 1000
						/ countDeleteFollow;

				System.out.println(event + " events in " + (time / 1000)
						+ " macro seconds");
				System.out.println(timePerEvent + " macro seconds per event");
				System.out.println(timePerRead + " macro seconds per read ("
						+ countRead + ")");
				System.out.println(timePerCreateUser
						+ " macro seconds per create user (" + countCreateUser
						+ ")");
				System.out.println(timePerCreateStatusUpdate
						+ " macro seconds per create status update ("
						+ countCreateStatusUpdate + ")");
				System.out.println(timePerCreateFollow
						+ " macro seconds per create follow ("
						+ countCreateFollow + ")");
				System.out.println(timePerDeleteFollow
						+ " macro seconds per delete follow ("
						+ countDeleteFollow + ")");

				time = 0;
				event = 0;
			}
		}

		reader.close();
	}

	private static void readStatusUpdateRequest(final Set<String> userIds)
			throws ClientProtocolException, IOException {
		String userId = null;
		final int userIndex = new Random().nextInt(userIds.size());
		int crrUserIndex = 0;
		for (Object crrUserId : userIds) {
			if (crrUserIndex == userIndex) {
				userId = (String) crrUserId;
			}
			crrUserIndex += 1;
		}

		timeRead = System.nanoTime();

		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Read.USER_IDENTIFIER, userId));
		params.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Read.POSTER_IDENTIFIER, userId));
		params.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Read.NUM_ITEMS, "15"));
		params.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Read.OWN_UPDATES, "0"));

		final String url = "http://localhost:8080/Graphity-Server-0.1/read?"
				+ URLEncodedUtils.format(params, "utf-8");
		final String response = readRequest(url);

		timeRead = System.nanoTime() - timeRead;
		time += timeRead;
		totalRead += timeRead;
		countRead += 1;

		if (new Random().nextInt(500) == 1) {
			System.out.println("HTTP GET " + url);
			System.out.println("response:" + response + " for read request "
					+ countRead);
		}
	}

	private static String readRequest(final String url)
			throws ClientProtocolException, IOException {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpGet httpGet = new HttpGet(url);

		final HttpResponse httpResponse = httpClient.execute(httpGet);
		final HttpEntity responseEntity = httpResponse.getEntity();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				responseEntity.getContent()));

		final StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append("\t");
		}
		return response.toString();
	}

	private static void createUserRequest(final String userId)
			throws ClientProtocolException, IOException {
		timeCreateUser = System.nanoTime();

		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(ProtocolConstants.Parameters.Create.TYPE, new StringBody(
					CreateRequestType.USER.getIdentifier()));
			entity.addPart(ProtocolConstants.Parameters.Create.User.USER_IDENTIFIER,
					new StringBody(userId));
			entity.addPart(ProtocolConstants.Parameters.Create.User.DISPLAY_NAME,
					new StringBody("user" + userId));
			entity.addPart(
					ProtocolConstants.Parameters.Create.User.PROFILE_PICTURE_PATH,
					new StringBody("http://google.de/somepic"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		postRequest(
				"http://localhost:8080/Graphity-Server-0.1/create",
				entity);
		timeCreateUser = System.nanoTime() - timeCreateUser;
		time += timeCreateUser;
		totalCreateUser += timeCreateUser;
		countCreateUser += 1;
	}

	private static void createStatusUpdateRequest(final String userId,
			final String statusUpdateId) throws ClientProtocolException,
			IOException {
		timeCreateStatusUpdate = System.nanoTime();

		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(
					ProtocolConstants.Parameters.Create.StatusUpdate.USER_IDENTIFIER,
					new StringBody(userId));
			entity.addPart(ProtocolConstants.Parameters.Create.TYPE, new StringBody(
					CreateRequestType.STATUS_UPDATE.getIdentifier()));
			entity.addPart(
					ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_IDENTIFIER,
					new StringBody(statusUpdateId));
			entity.addPart(
					ProtocolConstants.Parameters.Create.StatusUpdate.STATUS_UPDATE_TYPE,
					new StringBody("Plain"));
			entity.addPart(
					"message",
					new StringBody(
							"myDefaultStatusUpdateTextMessageDoesContainsAsMuchCharactersAsIWant!"));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		postRequest(
				"http://localhost:8080/Graphity-Server-0.1/create",
				entity);

		timeCreateStatusUpdate = System.nanoTime() - timeCreateStatusUpdate;
		time += timeCreateStatusUpdate;
		totalCreateStatusUpdate += timeCreateStatusUpdate;
		countCreateStatusUpdate += 1;
	}

	private static void createFollowEdge(final String followingId,
			final String followedId) throws ClientProtocolException,
			IOException {
		timeCreateFollow = System.nanoTime();

		final MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart(
					ProtocolConstants.Parameters.Create.Follow.USER_IDENTIFIER,
					new StringBody(followingId));
			entity.addPart(ProtocolConstants.Parameters.Create.TYPE, new StringBody(
					CreateRequestType.FOLLOW.getIdentifier()));
			entity.addPart(
					ProtocolConstants.Parameters.Create.Follow.FOLLOWED_IDENTIFIER,
					new StringBody(followedId));
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		postRequest(
				"http://localhost:8080/Graphity-Server-0.1/create",
				entity);

		timeCreateFollow = System.nanoTime() - timeCreateFollow;
		time += timeCreateFollow;
		totalCreateFollow += timeCreateFollow;
		countCreateFollow += 1;
	}

	private static void deleteFollowEdge(final String followingId,
			final String followedId) throws ClientProtocolException,
			IOException {
		timeDeleteFollow = System.nanoTime();

		final List<NameValuePair> values = new ArrayList<NameValuePair>(3);
		values.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Delete.Follow.USER_IDENTIFIER,
				followingId));
		values.add(new BasicNameValuePair(ProtocolConstants.Parameters.Delete.TYPE,
				DeleteRequestType.FOLLOW.getIdentifier()));
		values.add(new BasicNameValuePair(
				ProtocolConstants.Parameters.Delete.Follow.FOLLOWED_IDENTIFIER,
				followedId));
		final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);

		postRequest(
				"http://localhost:8080/Graphity-Server-0.1/delete",
				entity);

		timeDeleteFollow = System.nanoTime() - timeDeleteFollow;
		time += timeDeleteFollow;
		totalDeleteFollow += timeDeleteFollow;
		countDeleteFollow += 1;
	}

	private static String postRequest(final String url, final HttpEntity entity)
			throws ClientProtocolException, IOException {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);

		final HttpResponse httpResponse = httpClient.execute(httpPost);
		final HttpEntity responseEntity = httpResponse.getEntity();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				responseEntity.getContent()));

		final StringBuilder response = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
			response.append("\n");
		}
		return response.toString();
	}
}
