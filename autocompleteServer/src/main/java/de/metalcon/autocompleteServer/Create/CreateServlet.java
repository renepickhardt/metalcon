package de.metalcon.autocompleteServer.Create;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CreateServlet extends HttpServlet {

	public CreateServlet() {
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		ProcessCreateResponse resp = ProcessCreateRequest
				.checkRequestParameter(request, this.getServletContext());
		try {
			// response.setContentType("text/json");
			response.getWriter().write(resp.getResponse().toJSONString());
			response.getWriter().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
