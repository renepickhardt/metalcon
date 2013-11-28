package de.metalcon.middleware.controller;

import java.util.Calendar;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.metalcon.middleware.domain.Band;

@Controller
public class BandController {
    
    @RequestMapping(value = "/band/{bandName}", method = RequestMethod.GET)
    public String handleRequest(
            @PathVariable("bandName") String bandName,
            Model model) {
        Band band = new Band(bandName, Calendar.getInstance().getTime());
        
//        model.addAttribute("band", band);
        
        return "band";
    }
    
}
