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

public class GetIn extends HttpServlet{

	private static final long serialVersionUID = 4573770531900776448L;
	
	JSONObject jsonObj;
	JSONArray jsonArr;
	String muid;
	int[] foo = {23, 42, 1024, 666};
		
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		muid = request.getParameter("muid");
		if (muid == null)
			jsonObj.put("Error", ProtocolConstants.MUID_NOT_GIVEN);
		else if (muid.isEmpty())
			jsonObj.put("Error", ProtocolConstants.MUID_MALFORMED);
		else {
			
			for(int element : foo) {
				jsonArr.add(element);
			}
			
			jsonObj.put("muid", muid);
			jsonObj.put("edges", jsonArr);
			
			//TODO: API call goes here;
			//TODO: MUID validation;
			//TODO: Calling haveInCommonsService;
			
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
