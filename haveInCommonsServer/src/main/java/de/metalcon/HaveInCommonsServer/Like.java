package de.metalcon.HaveInCommonsServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.HaveInCommonsServer.helper.ProtocolConstants;
import de.metalcon.HaveInCommonsServer.Vote;

import org.json.simple.JSONObject;

public class Like extends HttpServlet{
	
	private static final long serialVersionUID = 4905364225284347988L;
	
	JSONObject jsonObj;
	String muid1, muid2;
	Vote vote;
		
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		muid1 = request.getParameter("muid1");
		muid2 = request.getParameter("muid2");
		try {
			vote = Vote.valueOf(request.getParameter("vote"));
		}
		catch (RuntimeException e) {
			System.out.println("[LIKE]: " + ProtocolConstants.VOTE_MALFORMED);
			e.printStackTrace();
			jsonObj.put("Error", ProtocolConstants.VOTE_MALFORMED);
		}
		if(muid1 == null || muid2 == null)
			jsonObj.put("Error", ProtocolConstants.MUID_NOT_GIVEN);
		else if (muid1.isEmpty() || muid2.isEmpty())
			jsonObj.put("Error", ProtocolConstants.MUID_MALFORMED);
		else if (vote == null)
			jsonObj.put("Error", ProtocolConstants.VOTE_NOT_GIVEN);
	
		//NormalizedLikeRetrieval	likeEngine = new NormalizedLikeRetrieval("/home/user/likeEngine/");
		
		long id1, id2;
		
		try {
			id1 = Integer.parseInt(muid1);
			id2 = Integer.parseInt(muid2);
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			jsonObj.put("Error", ProtocolConstants.MUID_MALFORMED);
		}
			
		//likeEngine.putEdge(id1, id2, vote.getString());
		
		jsonObj.put("Response", ProtocolConstants.REQUEST_VALID);
		
		response.setContentType("application/json");
		response(response, jsonObj.toJSONString());
	}
	
	private void response(HttpServletResponse resp, String msg)
			throws IOException {
		PrintWriter out = resp.getWriter();
		out.println(msg);
		out.close();
		
	}
}
