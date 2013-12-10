package de.metalcon.middleware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/music", method = RequestMethod.GET)
public class MusicController {
    
    @RequestMapping("{pathBand}")
    public ModelAndView handleBand(
            @PathVariable("pathBand") String pathBand) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/band", model);
    }
    
    @RequestMapping("{pathBand}/{pathRecord}")
    public ModelAndView handleRecord(
            @PathVariable("pathBand") String pathBand,
            @PathVariable("pathRecord") String pathRecord) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/record", model);
    }
    
    @RequestMapping("{pathBand}/{pathRecord}/{pathTrack}")
    public ModelAndView handleTrack(
            @PathVariable("pathBand") String pathBand,
            @PathVariable("pathRecord") String pathRecord,
            @PathVariable("pathTrack") String pathTrack) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/track", model);
    }

}
