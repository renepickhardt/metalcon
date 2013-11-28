package de.metalcon.middleware.config;

import java.util.Locale;

import org.codehaus.jackson.JsonEncoding;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class JsonViewResolver implements ViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale)
    throws Exception {
        MappingJacksonJsonView view = new MappingJacksonJsonView();
        view.setContentType(MediaType.APPLICATION_JSON_VALUE);
        view.setEncoding(JsonEncoding.UTF8);
        view.setPrettyPrint(true);
        return view;
    }
    
}
