package de.metalcon.middleware.controller.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.core.EntityUrlMapper;
import de.metalcon.middleware.domain.EntityType;
import de.metalcon.middleware.domain.Muid;
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
        Muid muid = getMuid(path1, path2, path3);
        return handleEmptyTab(muid);
    }
    
    private Muid getMuid(String path1, String path2, String path3)
            throws RedirectException {
        switch (getEntityType()) {
            case USER:       return urlMapper.getUser(path1);
            case BAND:       return urlMapper.getBand(path1);
            case RECORD:     return urlMapper.getRecord(path1, path2);
            case TRACK:      return urlMapper.getTrack(path1, path2, path3);
            case VENUE:      return urlMapper.getVenue(path1);
            case EVENT:      return urlMapper.getEvent(path1);
            case CITY:       return urlMapper.getCity(path1);
            case GENRE:      return urlMapper.getGenre(path1);
            case INSTRUMENT: return urlMapper.getInstrument(path1);
            case TOUR:       return urlMapper.getTour(path1);

            default:
                throw new IllegalStateException(
                        "Unimplemented EntityType for controller.");
        }
    }
   
}
