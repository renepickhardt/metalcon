package de.metalcon.middleware.controller.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.util.UrlMapper;

@Controller
@RequestMapping(value = UrlMapper.MUSIC_MAPPPING, method = RequestMethod.GET)
public class MusicController {
    
    @Autowired
    private UrlMapper urlMapper;
    
    @RequestMapping(UrlMapper.BAND_MAPPING)
    public ModelAndView handleBand(
            @PathVariable("pathBand") String pathBand) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/band", model);
    }
    
    @RequestMapping(UrlMapper.RECORD_MAPPING)
    public ModelAndView handleRecord(
            @PathVariable("pathBand") String pathBand,
            @PathVariable("pathRecord") String pathRecord) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/record", model);
    }
    
    @RequestMapping(UrlMapper.TRACK_MAPPING)
    public ModelAndView handleTrack(
            @PathVariable("pathBand") String pathBand,
            @PathVariable("pathRecord") String pathRecord,
            @PathVariable("pathTrack") String pathTrack) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/track", model);
    }

}
