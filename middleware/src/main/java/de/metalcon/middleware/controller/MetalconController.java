package de.metalcon.middleware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(method = RequestMethod.GET)
public abstract class MetalconController {
    
    protected void preRequest(Model model) {
    }
    
    protected String postRequest(String viewModel, Model model, String view) {
        if (viewModel.equals("true"))
            return "model";
        else
            return view;
    }

}
