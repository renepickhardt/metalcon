package de.metalcon.middleware.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import de.metalcon.middleware.util.JsonViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "de.metalcon.middleware" })
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
    
    @Override
    public void configureContentNegotiation(
            ContentNegotiationConfigurer configurer) {
        Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();
        mediaTypes.put("html", MediaType.APPLICATION_XHTML_XML);
        mediaTypes.put("json", MediaType.APPLICATION_JSON);
        
        configurer.replaceMediaTypes(mediaTypes)
                  .favorPathExtension(true)
                  .useJaf(false)
                  .defaultContentType(MediaType.APPLICATION_XHTML_XML);
    }
    
    @Bean
    public ViewResolver contentNegotiatingViewResolver(
            ContentNegotiationManager manager) {
        List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
        
        FreeMarkerViewResolver freeMarkerViewResolver =
                new FreeMarkerViewResolver();
        freeMarkerViewResolver
                .setContentType(MediaType.APPLICATION_XHTML_XML_VALUE);
        freeMarkerViewResolver.setCache(true);
        freeMarkerViewResolver.setPrefix("");
        freeMarkerViewResolver.setSuffix(".ftl");
        resolvers.add(freeMarkerViewResolver);
        
        JsonViewResolver jsonResolver =
                new JsonViewResolver();
        resolvers.add(jsonResolver);
        
        ContentNegotiatingViewResolver resolver =
                new ContentNegotiatingViewResolver();
        resolver.setViewResolvers(resolvers);
        resolver.setContentNegotiationManager(manager);
        return resolver;
    }
    
    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setDefaultEncoding("UTF-8");
        config.setTemplateLoaderPath("/WEB-INF/views/");
        return config;
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        return taskExecutor;
    }
    
}
