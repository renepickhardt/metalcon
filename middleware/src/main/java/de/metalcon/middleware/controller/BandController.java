package de.metalcon.middleware.controller;

import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.domain.Band;

@Controller
@RequestMapping(value = "/band", method = RequestMethod.GET)
public class BandController {
    
    @RequestMapping("")
    public ModelAndView handleRequest() {
        ModelAndView mv = new ModelAndView("band");
        return mv;
    }
    
    @RequestMapping("{bandName}")
    public ModelAndView handleRequestByBandName(
            @PathVariable("bandName") String bandName) {
        Band band = new Band(bandName, Calendar.getInstance().getTime());
        
        ModelAndView mv = new ModelAndView("band");
        //mv.addObject("band", band);
        return mv;
    }
    
}
