package de.metalcon.middleware.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import de.metalcon.middleware.view.entity.EntityView;

public abstract class MetalconView implements View {

    @Autowired
    private ViewResolver viewResolver;

    private View view;

    private String type;

    public MetalconView() {
        view = null;

        if (this instanceof EntityView)
            type = "entity";
        else
            throw new IllegalStateException("Unkown view type.");
    }

    @PostConstruct
    private void init() throws Exception {
        view = viewResolver.resolveViewName("page", Locale.GERMANY);
    }

    @Override
    public final String getContentType() {
        return view.getContentType();
    }

    @Override
    public final void render(
            Map<String, ?> model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Map<String, Object> m = new HashMap<String, Object>(model);
        m.put("view", this);
        view.render(m, request, response);
    }

    public String getType() {
        return type;
    }

}
