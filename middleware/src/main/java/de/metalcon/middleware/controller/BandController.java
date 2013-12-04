package de.metalcon.middleware.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.domain.Band;
import de.metalcon.middleware.util.request.JsonRequest;
import de.metalcon.middleware.util.request.RequestManager;
import de.metalcon.middleware.util.request.RequestTransaction;

@Controller
@RequestMapping(value = "/band", method = RequestMethod.GET)
public class BandController {
    
    @Autowired
    private RequestManager requestManager;

    @RequestMapping("")
    public ModelAndView handleRequest() {
        RequestTransaction tx = requestManager.startTransaction();
        tx.request(new JsonRequest("http://headers.jsontest.com/"));
        tx.request(new JsonRequest("http://ip.jsontest.com/"));

        List<String> jsonAnswers = new LinkedList<String>();

        String answer;
        while ((answer = tx.recieve()) != null) {
            jsonAnswers.add(answer);
        }

        ModelAndView mv = new ModelAndView("model");
        mv.addObject("jsonAnswers", jsonAnswers);
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
