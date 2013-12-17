package de.metalcon.middleware.springconfig;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

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
        DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
        wrapper.setExposeFields(false);
        wrapper.setExposureLevel(DefaultObjectWrapper.EXPOSE_SAFE);
        config.setObjectWrapper(wrapper);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setIncompatibleImprovements(new Version(2, 3, 20));
        config.setOutputEncoding("UTF-8");
    }

}
