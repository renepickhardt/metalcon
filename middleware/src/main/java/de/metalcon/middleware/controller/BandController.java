package de.metalcon.middleware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BandController extends MetalconController {
    
    @RequestMapping("/band/{bandName}")
    public String handleRequest(
            @PathVariable("bandName") String bandName,
            @RequestParam(value="model", required=false, defaultValue="")
                String viewModel,
            Model model) {
        preRequest(model);
        
        model.addAttribute("bandName", bandName);
        
        return postRequest(viewModel, model, "band");
    }

}
