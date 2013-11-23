package de.metalcon.middleware.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
	
	//private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
	    List<String> bands = new LinkedList<String>();
	    bands.add("Ensiferum");
	    bands.add("Manowar");
	    bands.add("Blind Guardian");
	    bands.add("Bolt Thrower");
	    
	    model.addAttribute("bands", bands);
	    return "model";
	}
	
}
