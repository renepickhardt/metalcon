package de.metalcon.server.tomcat.NSSP.read;

import javax.servlet.http.HttpServletRequest;

import org.neo4j.graphdb.Node;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.socialgraph.NeoUtils;

/**
 * read requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class ReadRequest {

	/**
	 * user requesting
	 */
	private final Node user;

	/**
	 * user posted
	 */
	private final Node poster;

	/**
	 * number of items to retrieve
	 */
	private final int numItems;

	/**
	 * retrieval flag
	 */
	private final boolean ownUpdates;

	/**
	 * create a new read request
	 * 
	 * @param user
	 *            user requesting
	 * @param poster
	 *            user posted
	 * @param numItems
	 *            number of items to retrieve
	 * @param ownUpdates
	 *            retrieval flag
	 */
	public ReadRequest(final Node user, final Node poster, final int numItems,
			final boolean ownUpdates) {
		this.user = user;
		this.poster = poster;
		this.numItems = numItems;
		this.ownUpdates = ownUpdates;
	}

	/**
	 * 
	 * @return user requesting
	 */
	public Node getUser() {
		return this.user;
	}

	/**
	 * 
	 * @return user posted
	 */
	public Node getPoster() {
		return this.poster;
	}

	/**
	 * 
	 * @return number of items to retrieve
	 */
	public int getNumItems() {
		return this.numItems;
	}

	/**
	 * 
	 * @return retrieval flag
	 */
	public boolean getOwnUpdates() {
		return this.ownUpdates;
	}

	/**
	 * check a read request for validity concerning NSSP
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param readResponse
	 *            read response object
	 * @return read request object<br>
	 *         <b>null</b> if the read request is invalid
	 */
	public static ReadRequest checkRequest(final HttpServletRequest request,
			final ReadResponse readResponse) {
		final Node user = checkUserIdentifier(request, readResponse);
		if (user != null) {
			final Node poster = checkPosterIdentifier(request, readResponse);
			if (poster != null) {
				final int numItems = checkNumItems(request, readResponse);
				if (numItems != 0) {
					final Boolean ownUpdates = checkOwnUpdates(request,
							readResponse);
					if (ownUpdates != null) {
						return new ReadRequest(user, poster, numItems,
								ownUpdates);
					}
				}
			}
		}

		return null;
	}

	/**
	 * check if the request contains a valid user identifier
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkUserIdentifier(final HttpServletRequest request,
			final ReadResponse response) {
		final String userId = request
				.getParameter(ProtocolConstants.Parameters.Read.USER_IDENTIFIER);
		if (userId != null) {
			final Node user = NeoUtils.getUserByIdentifier(userId);
			if (user != null) {
				return user;
			}
			response.userIdentifierInvalid(userId);
		} else {
			response.userIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid poster identifier
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return user node with the identifier passed<br>
	 *         <b>null</b> if the identifier is invalid
	 */
	private static Node checkPosterIdentifier(final HttpServletRequest request,
			final ReadResponse response) {
		final String posterId = request
				.getParameter(ProtocolConstants.Parameters.Read.POSTER_IDENTIFIER);
		if (posterId != null) {
			final Node user = NeoUtils.getUserByIdentifier(posterId);
			if (user != null) {
				return user;
			}
			response.posterIdentifierInvalid(posterId);
		} else {
			response.posterIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid number of items to retrieve
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return number of items to retrieve<br>
	 *         zero if the number is invalid
	 */
	private static int checkNumItems(final HttpServletRequest request,
			final ReadResponse response) {
		final String sNumItems = request
				.getParameter(ProtocolConstants.Parameters.Read.NUM_ITEMS);
		if (sNumItems != null) {
			int numItems;
			try {
				numItems = Integer.valueOf(sNumItems);
				if (numItems > 0) {
					return numItems;
				}

				response.numItemsInvalid("Please provide a number greater than zero.");
			} catch (final NumberFormatException e) {
				response.numItemsInvalid("\""
						+ sNumItems
						+ "\" is not a number. Please provide a number such as \"15\" to retrieve that number of items.");
			}
		} else {
			response.numItemsMissing();
		}

		return 0;
	}

	/**
	 * check if the request contains a valid retrieval flag
	 * 
	 * @param request
	 *            Tomcat servlet request
	 * @param response
	 *            response object
	 * @return retrieval flag<br>
	 *         <b>null</b> if the retrieval flag is invalid
	 */
	private static Boolean checkOwnUpdates(final HttpServletRequest request,
			final ReadResponse response) {
		final String sOwnUpdates = request
				.getParameter(ProtocolConstants.Parameters.Read.OWN_UPDATES);
		if (sOwnUpdates != null) {
			try {
				final int iOwnUpdates = Integer.valueOf(sOwnUpdates);
				return (iOwnUpdates != 0);
			} catch (final NumberFormatException e) {
				response.ownUpdatesInvalid(sOwnUpdates);
			}
		} else {
			response.ownUpdatesMissing();
		}

		return null;
	}

}