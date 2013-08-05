package de.metalcon.autocompleteServer;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import de.metalcon.autocompleteServer.Search;

/**
 * Servlet implementation class TestServlet
 */
//@WebServlet("/suggest")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");	//is this necessary?

		String docType =
				"<!doctype html public \"-//w3c//dtd html 4.0 " +
						"transitional//en\">\n";
		
		//this must be modified so it calls the Search-class methods I'm working on now
		
		String SearchRequest = request.getParameter("Search_Term");
		
		
		//TODO: Find an approach wo not only change but to eleminate null-Pointer problems, which occur if there is no search term GET parameter
		if (!(SearchRequest.equals(null)))
			SearchRequest = ("NoBand");	
		String SearchResult = Search.treeSearch(request.getParameter("Search_Term"));


		//actual HTML output is constructed and sent here
		
		PrintWriter out = response.getWriter();
		out.println(docType +
				"<html>\n" +
				"<head><title>" + " Test-Servlet for SuggestTree-Search " + "</title></head>\n" +
				"<body bgcolor=\"#f0f0f0\">\n" +
				"<h1 align=\"center\">" + " Titel " + "</h1>\n" +
				"<b>Search Term</b>: "
				+ request.getParameter("Search_Term") + "\n" +
				"  <b>Search Result</b>: " + SearchResult +
				"</body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
