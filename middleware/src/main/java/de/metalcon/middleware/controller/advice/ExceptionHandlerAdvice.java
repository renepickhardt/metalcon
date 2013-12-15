package de.metalcon.middleware.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

import de.metalcon.middleware.exception.RedirectException;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    
    @ExceptionHandler(RedirectException.class)
    public View handleRedirectException(RedirectException e) {
        return e.getRedirectView();
    }
    
}
