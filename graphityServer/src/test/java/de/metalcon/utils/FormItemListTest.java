package de.metalcon.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.utils.FormItemList;

public class FormItemListTest {

	/**
	 * valid content type
	 */
	private static final String VALID_CONTENT_TYPE = "multipart/form-data";

	/**
	 * invalid content type
	 */
	private static final String INVALID_CONTENT_TYPE = "application/x-www-form-urlencoded";

	/**
	 * Tomcat request servlet
	 */
	private HttpServletRequest request;

	/**
	 * form item list being tested
	 */
	private FormItemList formItemList;

	@Before
	public void setUp() {
		this.request = mock(HttpServletRequest.class);
	}

	private void fillRequest(final String contentType) {
		final String boundary = "===myGreatBoundary===";
		when(this.request.getContentType()).thenReturn(contentType);
		// TODO
	}

	@Test
	public void testNoMultipart() {
		this.fillRequest(INVALID_CONTENT_TYPE);
	}

}