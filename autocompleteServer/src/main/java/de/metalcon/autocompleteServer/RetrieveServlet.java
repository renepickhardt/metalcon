package de.metalcon.autocompleteServer;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.metalcon.autocompleteServer.Search;
import de.metalcon.autocompleteServer.Helper.ProcessRetrieveRequest;

/**
 * Servlet implementation class TestServlet
 */
//@WebServlet("/suggest")
public class RetrieveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RetrieveServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProcessRetrieveRequest.checkRequestParameter(request, getServletContext());
		response.setContentType("text/json");	//is this necessary?

		//this must be modified so it calls the Search-class methods I'm working on now
		
		String SearchResult = null;
				
		String SearchRequest = request.getParameter("Search_Term");
		long timePre = 0;
		long timePost = 0;
		long timeSpent = 0;
		//TODO: Find an approach wo not only change but to eleminate null-Pointer problems, which occur if there is no search term GET parameter
		if (SearchRequest != null ){
		//	SearchRequest = ("NoBand");	
		// String SearchResult = Search.treeSearch(SearchRequest);
		timePre = System.nanoTime();
		SearchResult = Search.treeSearch(request.getParameter("Search_Term"));
		timePost = System.nanoTime();
		timeSpent = timePost - timePre;}
		//actual HTML output is constructed and sent here
		
		PrintWriter out = response.getWriter();
		out.println("[" + SearchResult + timeSpent + "]");
		out.flush();
	}
	
}

