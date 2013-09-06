package de.metalcon.autocompleteServer.Create;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;
import de.metalcon.utils.FormItemList;

public class TestCreateNewIndex {

	final private ServletConfig servletConfig = mock(ServletConfig.class);
	final private ServletContext servletContext = mock(ServletContext.class);

	@Before
	public void initializeTest() {

		HttpServlet servlet = mock(HttpServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);
		SuggestTree generalIndex = new SuggestTree(7);
		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ "testIndex")).thenReturn(generalIndex);

		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ "defaultIndex")).thenReturn(generalIndex);

		try {
			servlet.init(this.servletConfig);
		} catch (ServletException e) {
			fail("could not initialize servlet");
			e.printStackTrace();
		}
	}

	@Test
	public void testMissingIndexName() {

		NewIndexResponse testResponse = this.processNewIndexRequest(null);

		if (testResponse.getResponse().containsKey("Error:IndexNameNotGiven")) {
			assertEquals(CreateStatusCodes.INDEXNAME_NOT_GIVEN, testResponse
					.getResponse().get("Error:IndexNameNotGiven"));
		} else {
			fail("Error-Message missing!");
		}
		fail("Not yet implemented");
	}

	private NewIndexResponse processNewIndexRequest(String indexName) {

		NewIndexResponse response = new NewIndexResponse(
				this.servletConfig.getServletContext());
		FormItemList formItemList = new FormItemList();

		if (indexName != null) {
			formItemList.addField(ProtocolConstants.INDEX_PARAMETER, indexName);
		}

		return NewIndexRequest.checkRequestParameter(formItemList, response,
				this.servletConfig.getServletContext());
	}

}
