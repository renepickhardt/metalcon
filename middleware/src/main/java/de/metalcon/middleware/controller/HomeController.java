package de.metalcon.middleware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	
	//private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home() {
	    ModelAndView mv = new ModelAndView("home");
	    mv.addObject("message", "Metalcon Middleware Online");
	    return mv;
	}
	
}
