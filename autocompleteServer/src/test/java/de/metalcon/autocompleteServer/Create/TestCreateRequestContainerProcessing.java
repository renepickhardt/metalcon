package de.metalcon.autocompleteServer.Create;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;
import de.metalcon.autocompleteServer.Helper.SuggestTree;

public class TestCreateRequestContainerProcessing {

	final private ServletContext servletContext = mock(ServletContext.class);
	final private ServletConfig servletConfig = mock(ServletConfig.class);

	CreateRequestContainer testContainer = new CreateRequestContainer(
			this.servletContext);

	SuggestTree testIndex = new SuggestTree(7);

	@Before
	public void initializeTest() {
		CreateServlet servlet = mock(CreateServlet.class);
		when(this.servletConfig.getServletContext()).thenReturn(
				this.servletContext);

		when(
				this.servletContext
						.getAttribute(ProtocolConstants.INDEX_PARAMETER
								+ ProtocolTestConstants.VALID_SUGGESTION_INDEX))
				.thenReturn(this.testIndex);

		this.testContainer.setRequestServlet(servlet);

		this.testContainer.getComponents().setIndexName(
				ProtocolTestConstants.VALID_SUGGESTION_INDEX);
		this.testContainer.getComponents().setKey(
				ProtocolTestConstants.VALID_SUGGESTION_KEY);
		this.testContainer.getComponents().setSuggestString(
				ProtocolTestConstants.VALID_SUGGESTION_STRING);
		this.testContainer
				.getComponents()
				.setWeight(
						Integer.parseInt(ProtocolTestConstants.VALID_SUGGESTION_WEIGHT));

	}

	@Test
	public void test() {
		this.testContainer.run();
	}

}
