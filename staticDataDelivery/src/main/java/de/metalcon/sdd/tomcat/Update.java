package de.metalcon.sdd.tomcat;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidAttrNameException;
import de.metalcon.sdd.error.InvalidTypeException;

public class Update extends Servlet {

    private static final long serialVersionUID = 6171111615128520290L;
    
    @Override
    protected String run(HttpServletRequest request)
    throws InvalidTypeException, InvalidAttrException, InvalidAttrNameException {
        Map<String, String[]> params = request.getParameterMap();
        
        long   id   = 0;
        String type = null;
        Map<String, String>   attrs  = new HashMap<String, String>();
        
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String   attrName  = param.getKey(); 
            String[] attrValue = param.getValue();
            if (attrValue != null && attrValue.length >= 0) {
                if (attrName.equals("id"))
                    id = Long.parseLong(attrValue[0]);
                else if (attrName.equals("type"))
                    type = attrValue[0];
                else
                    attrs.put(attrName, attrValue[0]);
            }
        }
        
        if (id == 0)
            throw new RuntimeException("no id");
        if (type == null)
            throw new RuntimeException("no type");
        
        if (sdd.updateEntity(id, type, attrs))
            return "update queued";
        else
            return "queueing update failed";
    }

}
