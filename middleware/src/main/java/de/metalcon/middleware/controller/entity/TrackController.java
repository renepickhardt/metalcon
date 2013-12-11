package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.core.EntityUrlMapper;

@Controller
@RequestMapping(value  = EntityUrlMapper.TRACK_MAPPING,
                method = RequestMethod.GET)
public class TrackController extends EntityController {
    
    @RequestMapping("")
    public ModelAndView handle(
            @PathVariable("pathBand") String pathBand,
            @PathVariable("pathRecord") String pathRecord,
            @PathVariable("pathTrack") String pathTrack) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/track", model);
    }

}
