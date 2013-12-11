package de.metalcon.middleware.controller.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;
import de.metalcon.middleware.exception.RedirectException;

public abstract class EntityController extends MetalconController {

    @Autowired
    protected EntityUrlMapper urlMapper;
    
    protected abstract EntityType getEntityType();
    
    protected abstract ModelAndView handleEmptyTab(Muid muid);
    
    @RequestMapping(EntityUrlMapper.EMPTY_TAB_MAPPING)
    public ModelAndView requestEmptyTab(
            @PathVariable("path1") String path1,
            @PathVariable("path2") String path2,
            @PathVariable("path3") String path3)
    throws RedirectException {
        Muid muid = urlMapper.getMuid(
                getEntityType(), path1, path2, path3);
        return handleEmptyTab(muid);
    }
    
    
   
}
