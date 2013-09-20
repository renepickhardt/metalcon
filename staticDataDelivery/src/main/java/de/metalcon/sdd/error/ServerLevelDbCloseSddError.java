package de.metalcon.sdd.error;

public class ServerLevelDbCloseSddError extends SddError {

    private static final long serialVersionUID = 6181356782607203478L;

    @Override
    public String getDescription() {
        return "Closing LevelDB failed.";
    }

    @Override
    public String getSuggestion() {
        return "Make sure LevelDB closed properly.\n" +
               "You might have to delete the .lock file manually.";
    }

}
