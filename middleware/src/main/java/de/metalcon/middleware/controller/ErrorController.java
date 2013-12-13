package de.metalcon.middleware.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController extends MetalconController {

    @RequestMapping("")
    public ModelAndView handleError(HttpServletRequest request) {
        int statusCode = (Integer)
                request.getAttribute("javax.servlet.error.status_code");
        Throwable exception = (Throwable)
                request.getAttribute("javax.servlet.error.exception");
        
        switch (statusCode) {
            case 404: return handleNotFoundError(exception);
            default:  return handleGenericError(exception, statusCode);
        }
    }
    
    private ModelAndView handleNotFoundError(Throwable exception) {
        ModelMap model = new ModelMap();
        return new ModelAndView("error/404", model);
    }
    
    private ModelAndView handleGenericError(Throwable exception, Integer statusCode) {
        ModelMap model = new ModelMap();
        model.addAttribute("statusCode",    statusCode);
        model.addAttribute("statusMessage", getStatusMessage(statusCode));
        
        if (exception != null) {
            StringWriter exceptionTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(exceptionTrace));
            
            model.addAttribute("exception", exceptionTrace.toString());
        }
        
        return new ModelAndView("error/generic", model);
    }
    
    private String getStatusMessage(int statusCode) {
        HttpStatus status = HttpStatus.valueOf(statusCode);
        return status.getReasonPhrase();
    }
    
}
