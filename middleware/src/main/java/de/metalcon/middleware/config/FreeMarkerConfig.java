package de.metalcon.middleware.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Component
public class FreeMarkerConfig extends FreeMarkerConfigurer {
    
    public FreeMarkerConfig() {
        super();
        setDefaultEncoding("UTF-8");
        setTemplateLoaderPath("/WEB-INF/views/");
    }
    
    @Override
    protected void postProcessConfiguration(Configuration config)
    throws IOException, TemplateException {
        config.setTemplateExceptionHandler(
                TemplateExceptionHandler.RETHROW_HANDLER);
    }

}
