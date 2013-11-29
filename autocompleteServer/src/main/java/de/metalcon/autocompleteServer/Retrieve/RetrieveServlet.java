package de.metalcon.autocompleteServer.Retrieve;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestServlet
 */
// @WebServlet("/suggest")
public class RetrieveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RetrieveServlet() {
		super();
	}

	/**
	 * de.metalcon.autocompleteServer.Retrieve.RetrieveServlet
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ProcessRetrieveResponse responseObject = ProcessRetrieveRequest
				.checkRequestParameter(request, this.getServletContext());
		String resultJson = responseObject.buildJsonResonse();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println(resultJson);
		out.flush();
	}

}
