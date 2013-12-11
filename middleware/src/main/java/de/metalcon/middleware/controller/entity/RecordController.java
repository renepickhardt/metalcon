package de.metalcon.middleware.controller.entity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.domain.Muid;
import de.metalcon.middleware.domain.entity.EntityType;

@Controller
@RequestMapping(value  = EntityUrlMapper.RECORD_MAPPING,
                method = RequestMethod.GET)
public class RecordController extends EntityController {

    @Override
    protected EntityType getEntityType() {
        return EntityType.RECORD;
    }

    @Override
    protected ModelAndView handleEmptyTab(Muid muid) {
        ModelMap model = new ModelMap();
        return new ModelAndView("entity/record", model);
    }
    
}
