package de.metalcon.middleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.metalcon.middleware.controller" })
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
    
    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setContentType("application/xhtml+xml; charset=UTF-8");
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        return resolver;
    }
    
    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setDefaultEncoding("UTF-8");
        config.setTemplateLoaderPath("/WEB-INF/views/");
        return config;
    }
    
}
