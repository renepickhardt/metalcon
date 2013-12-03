package de.metalcon.middleware.controller;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	//private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
	    List<String> bands = new LinkedList<String>();
	    bands.add("Ensiferum");
	    bands.add("Manowar");
	    bands.add("Blind Guardian");
	    bands.add("Bolt Thrower");
	    bands.add("another test");
	    
	    ModelAndView mv = new ModelAndView("home");
	    mv.addObject("bands", bands);
	    return mv;
	}
	
}
