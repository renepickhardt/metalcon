package de.metalcon.sddServer.error;

import de.metalcon.sddServer.IdDetail;

public class ServerDetailNoneSddError extends SddError {

    private static final long serialVersionUID = 2023989886429835969L;
    
    IdDetail idDetail;
    
    public ServerDetailNoneSddError(IdDetail idDetail) {
        this.idDetail = idDetail;
    }

    @Override
    public String getDescription() {
        return "Can't read entity with Detail.NONE.";
    }

    @Override
    public String getSuggestion() {
        return "Entity read with this IdDetail: \"" + idDetail.toString() + "\"." +
               "The Detail must not be \"none\".";
    }

}
