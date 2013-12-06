package de.metalcon.middleware.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.util.request.JsonRequest;
import de.metalcon.middleware.util.request.RequestTransaction;

@Controller
@RequestMapping(value = "/news", method = RequestMethod.GET)
public class NewsController {
    
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BeanFactory beanFactory;
    
    @RequestMapping("")
    public ModelAndView handle() {
        return new ModelAndView("news");
    }
    
    @RequestMapping("{userId}/{posterId}/{ownUpdates}")
    public ModelAndView listNews(
            @PathVariable("userId")     String  userId,
            @PathVariable("posterId")   String  posterId,
            @PathVariable("ownUpdates") Boolean ownUpdates)
    throws JsonParseException, JsonMappingException, IOException {
        RequestTransaction tx = beanFactory.getBean(RequestTransaction.class);
        tx.request(new JsonRequest(
                "http://localhost:8080/Graphity-Server-0.1/read?" +
                        "user_id=" + userId +
                        "&poster_id=" + posterId +
                        "&num_items=10" +
                        "&own_updates=" + (ownUpdates ? "1" : "0")));
        
        List<Map<String, Map<String, String>>> modelNews =
                new LinkedList<Map<String, Map<String, String>>>();
        
        String   answer = (String) tx.recieve();
        JsonNode root   = mapper.readValue(answer, JsonNode.class);
        for (JsonNode item : root.path("items")) {
            JsonNode verb   = item.get("verb");
            JsonNode actor  = item.get("actor");
            JsonNode object = item.get("object");
            
            Map<String, String> modelVerb   = new HashMap<String, String>();
            modelVerb.put("value", verb.getTextValue());
            
            Map<String, String> modelActor  = new HashMap<String, String>();
            modelActor.put("id",          actor.get("id").getTextValue());
            modelActor.put("objectType",  actor.get("objectType").getTextValue());
            modelActor.put("displayName", actor.get("displayName").getTextValue());
            
            Map<String, String> modelObject = new HashMap<String, String>();
            modelObject.put("id",         object.get("id").getTextValue());
            modelObject.put("objectType", object.get("objectType").getTextValue());
            modelObject.put("message",    object.get("message").getTextValue());
            modelObject.put("type",       object.get("type").getTextValue());
            
            Map<String, Map<String, String>> modelItem =
                    new HashMap<String, Map<String, String>>();
            modelItem.put("verb",   modelVerb);
            modelItem.put("actor",  modelActor);
            modelItem.put("object", modelObject);
            modelNews.add(modelItem);
        }
        
        ModelMap model = new ModelMap();
        model.addAttribute("userId",     userId);
        model.addAttribute("posterId",   posterId);
        model.addAttribute("ownUpdates", ownUpdates);
        model.addAttribute("news",       modelNews);
        return new ModelAndView("news", model);
    }
    
    @RequestMapping(value  = "{userId}/{posterId}/{ownUpdates}/post",
                    method = RequestMethod.POST)
    public String postNews(
            @PathVariable("userId")     String  userId,
            @PathVariable("posterId")   String  posterId,
            @PathVariable("ownUpdates") Boolean ownUpdates, 
            @RequestParam("formMessage") String formMessage) throws IOException {
    	//FIXME: make nice!
    	String statusUpdateId = System.currentTimeMillis()+"";
    	MultipartEntity mpe = new MultipartEntity();        
    	mpe.addPart("user_id", new StringBody(userId));
    	mpe.addPart("status_update_id",new StringBody(statusUpdateId));
    	mpe.addPart("status_update_type", new StringBody("Plain"));
    	mpe.addPart("message", new StringBody(formMessage));
    	mpe.addPart("type", new StringBody("status_update"));

    	String url = "http://localhost:8080/Graphity-Server-0.1/create";
    	HttpClient httpclient = new DefaultHttpClient();
    	HttpPost httpPost = new HttpPost(url);
    	httpPost.setEntity(mpe);
    	HttpResponse response = httpclient.execute(httpPost);
    	BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    	int i = 0;
    	while ((i = br.read()) >= 0){
    		System.out.print((char)i);
    	}
    	
//    	rt.exchange(url, HttpMethod.POST, (HttpEntity<?>)mpe, String.class);
//    	String resp = rt.postForObject(url, mvm, String.class,mvm );
   // 	System.out.println("danach" + resp);

        return "redirect:/news/" + userId + "/" + posterId + "/" + ownUpdates.toString();
    }
    
    public class FormData{
    	/**
		 * @return the formMessage
		 */
		public String getFormMessage() {
			return formMessage;
		}
		public FormData(){
			
		}
		/**
		 * @param formMessage the formMessage to set
		 */
		public void setFormMessage(String formMessage) {
			this.formMessage = formMessage;
		}

		private String formMessage;    	
    }
    
}
