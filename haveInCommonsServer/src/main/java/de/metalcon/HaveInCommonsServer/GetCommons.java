package de.metalcon.HaveInCommonsServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.HaveInCommonsServer.helper.ProtocolConstants;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetCommons extends HttpServlet{

	private static final long serialVersionUID = 6604402339839516526L;
	
	JSONObject jsonObj;
	JSONArray jsonArr;
	String muid1, muid2;
	int[] foo = {23, 42, 1024, 666};
			
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException{
		muid1 = request.getParameter("muid1");
		muid2 = request.getParameter("miud2");
		if (muid1 == null || muid2 == null)
			jsonObj.put("Error", ProtocolConstants.MUID_NOT_GIVEN);
		else if (muid1.isEmpty() || muid2.isEmpty())
			jsonObj.put("Error", ProtocolConstants.MUID_MALFORMED);
		else {
			
			for(int val : foo) {
				jsonArr.add(val);
			}

			//TODO: API call goes here;
			//TODO: MUID validation;
			//TODO: Calling haveInCommonsService;
			
			jsonObj.put("muid-from", muid1);
			jsonObj.put("muid-to", muid2);
			jsonObj.put("edges", jsonArr);
		}
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
