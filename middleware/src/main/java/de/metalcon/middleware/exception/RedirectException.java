package de.metalcon.middleware.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.view.RedirectView;

public class RedirectException extends Exception {

    private static final long serialVersionUID = 3867547173601947634L;
    
    private String path;
    
    private boolean permanent;
   
    public RedirectException(String path) {
        this(path, false);
    }
    
    public RedirectException(String path, boolean permanent) {
        this.path      = path;
        this.permanent = permanent;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean getPermanent() {
        return permanent;
    }
    
    public RedirectView getRedirectView()  {
        RedirectView view = new RedirectView(path, true);
        if (permanent)
            view.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return view;
    }

}
