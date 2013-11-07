package de.metalcon.sdd.tomcat;

import javax.servlet.http.HttpServletRequest;

import de.metalcon.sdd.error.InvalidDetailException;

public class Read extends Servlet {

    private static final long serialVersionUID = 7060493709035083463L;
    
    @Override
    protected String run(HttpServletRequest request)
    throws InvalidDetailException {
        String query = request.getQueryString();
        if (query == null)
            throw new RuntimeException("no query");
        
        String[] querySplit = query.split(config.getIdDetailDelimeter(), 2);
        if (querySplit.length != 2)
            throw new RuntimeException("illegal query format");
        
        long   id     = Long.parseLong(querySplit[0]);
        String detail = querySplit[1];
        
        return sdd.readEntity(id, detail);
    }
    
}
