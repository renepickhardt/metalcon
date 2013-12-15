package de.metalcon.middleware.controller;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import static javax.servlet.RequestDispatcher.*;

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
        Throwable exception  = (Throwable) request.getAttribute(ERROR_EXCEPTION);
        String    requestUri = (String)    request.getAttribute(ERROR_REQUEST_URI);
        Integer   statusCode = (Integer)   request.getAttribute(ERROR_STATUS_CODE);
        
        switch (statusCode) {
            case 404:
                return handleNotFoundError(exception, requestUri);
            default:
                return handleGenericError(exception, requestUri, statusCode);
        }
    }
    
    private ModelAndView handleNotFoundError(
            Throwable exception, String requestUri) {
        ModelMap model = new ModelMap();
        model.addAttribute("requestUri", requestUri);
        return new ModelAndView("error/error404", model);
    }
    
    private ModelAndView handleGenericError(
            Throwable exception, String requestUri, Integer statusCode) {
        ModelMap model = new ModelMap();
        model.addAttribute("requestUri",    requestUri);
        model.addAttribute("statusCode",    statusCode);
        model.addAttribute("statusMessage", getStatusMessage(statusCode));
        
        if (exception != null) {
            StringWriter exceptionTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(exceptionTrace));
            
            model.addAttribute("exception", exceptionTrace.toString());
        }
        
        return new ModelAndView("error/error", model);
    }
    
    private String getStatusMessage(int statusCode) {
        HttpStatus status = HttpStatus.valueOf(statusCode);
        return status.getReasonPhrase();
    }
    
}
