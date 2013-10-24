package de.metalcon.HaveInCommonsServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

public class Like extends HttpServlet{
	
	private static final long serialVersionUID = 4905364225284347988L;
	
	JSONObject jsonObj;
		
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	String uuid1 = request.getParameter("entity1");
	String uuid2 = request.getParameter("entity2");
	
	//TODO: Concatenate JSON string with entity1 and entity2
	
	jsonObj.put("uuid1", uuid1);
	jsonObj.put("uuid2", uuid2);
	
	//TODO: API call goes here;
	//TODO: Parameter validation;
	//TODO: MUID validation;
	//TODO: Calling haveInCommonsService;
	
	
	response(response, jsonObj.toJSONString());
	
	}
	
	private void response(HttpServletResponse resp, String msg)
			throws IOException {
		PrintWriter out = resp.getWriter();
		out.println(msg);
		
	}
}
