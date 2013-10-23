package de.metalcon.sddServer.error;

public class ServerLevelDbInitializationSddError extends SddError {

    private static final long serialVersionUID = 6400975919573613975L;
    
    @Override
    public String getDescription() {
        return "Initializing LevelDB failed.";
    }
    
    @Override
    public String getSuggestion() {
        return "Make sure the process running LevelDB has write-access to the LevelDB files.\n" +
               "Make sure LevelDB isn't currently opened by another process.";
    }

}
