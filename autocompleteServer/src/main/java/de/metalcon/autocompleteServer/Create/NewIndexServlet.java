package de.metalcon.autocompleteServer.Create;

import java.io.IOException;

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
		
		final NewIndexContainer container = resp.getContainer();

		if (container != null) {
			// stack command
			container.setRequestServlet(this);
			this.commandQueue.add(container);

			// wait for the command to be finished
			try {
				this.responseQueue.take();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			response.getWriter().write(resp.getResponse().toJSONString());
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	}
}