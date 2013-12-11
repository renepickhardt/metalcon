package de.metalcon.middleware.controller.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.core.request.JsonRequest;
import de.metalcon.middleware.core.request.RequestTransaction;

@Controller
@RequestMapping(value = "/test/news", method = RequestMethod.GET)
public class TestNewsController {
    
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
        
        List<Map<String, Object>> modelNews =
                new LinkedList<Map<String, Object>>();
        
        String   answer = (String) tx.recieve();
        JsonNode root   = mapper.readValue(answer, JsonNode.class);
        for (JsonNode item : root.path("items")) {
            JsonNode verb      = item.get("verb");
            JsonNode actor     = item.get("actor");
            JsonNode object    = item.get("object");
            JsonNode published = item.get("published");
            
            Map<String, String> modelActor  = new HashMap<String, String>();
            modelActor.put("id",          actor.get("id").getTextValue());
            modelActor.put("objectType",  actor.get("objectType").getTextValue());
            modelActor.put("displayName", actor.get("displayName").getTextValue());
            
            Map<String, String> modelObject = new HashMap<String, String>();
            modelObject.put("id",         object.get("id").getTextValue());
            modelObject.put("objectType", object.get("objectType").getTextValue());
            modelObject.put("message",    object.get("message").getTextValue());
            modelObject.put("type",       object.get("type").getTextValue());
            
            Map<String, Object> modelItem = new HashMap<String, Object>();
            modelItem.put("verb",      verb.getTextValue());
            modelItem.put("actor",     modelActor);
            modelItem.put("object",    modelObject);
            modelItem.put("published", published.getTextValue());
            modelNews.add(modelItem);
        }
        
        ModelMap model = new ModelMap();
        model.addAttribute("userId",     userId);
        model.addAttribute("posterId",   posterId);
        model.addAttribute("ownUpdates", ownUpdates);
        model.addAttribute("news",       modelNews);
        return new ModelAndView("test/news", model);
    }
    
    @RequestMapping(value  = "{userId}/{posterId}/{ownUpdates}/post",
                    method = RequestMethod.POST)
    public String postNews(
            @PathVariable("userId")      String  userId,
            @PathVariable("posterId")    String  posterId,
            @PathVariable("ownUpdates")  Boolean ownUpdates, 
            @RequestParam("formMessage") String  formMessage)
    throws IOException {
    	MultipartEntity entity = new MultipartEntity();        
    	entity.addPart("user_id",            new StringBody(userId));
    	entity.addPart("status_update_id",
    	        new StringBody(String.valueOf(System.currentTimeMillis())));
    	entity.addPart("status_update_type", new StringBody("Plain"));
    	entity.addPart("message",            new StringBody(formMessage));
    	entity.addPart("type",               new StringBody("status_update"));

    	String   url      = "http://localhost:8080/Graphity-Server-0.1/create";
    	HttpPost httpPost = new HttpPost(url);
    	httpPost.setEntity(entity);
    	new DefaultHttpClient().execute(httpPost);
    	
        return "redirect:/test/news/" + userId + "/" + posterId + "/" + ownUpdates.toString();
    }
    
}
