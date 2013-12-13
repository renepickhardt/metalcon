package de.metalcon.middleware.controller.test;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/test", method = RequestMethod.GET)
public class TestHomeController {
	
	//private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping("")
	public ModelAndView home() {
	    List<String> bands = new LinkedList<String>();
	    bands.add("Ensiferum");
	    bands.add("Manowar");
	    bands.add("Blind Guardian");
	    bands.add("Bolt Thrower");
	    bands.add("another test");
	    
	    ModelMap model = new ModelMap();
	    model.addAttribute("bands", bands);
	    return new ModelAndView("test/home", model);
	}
	
    @RequestMapping("ftlerror")
    public ModelAndView handleFtlError() {
        return new ModelAndView("test/ftlerror");
    }
    
}
