package de.metalcon.autocompleteServer.Create;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewIndexServlet extends HttpServlet {

	public NewIndexServlet() {
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);

		final ServletContext context = config.getServletContext();
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		NewIndexResponse resp = NewIndexRequest.handleServlet(request,
				this.getServletContext());

	}
}