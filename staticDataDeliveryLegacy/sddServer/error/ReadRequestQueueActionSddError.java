package de.metalcon.sddServer.error;

public class ReadRequestQueueActionSddError extends SddError {

    private static final long serialVersionUID = -8975798643344351996L;
    
    @Override
    public String getDescription() {
        return "No Queue Action for ReadRequests.";
    }
    
    @Override
    public String getSuggestion() {
        return "ReadRequests are not designed to be pushed on the server queue.";
    }

}
