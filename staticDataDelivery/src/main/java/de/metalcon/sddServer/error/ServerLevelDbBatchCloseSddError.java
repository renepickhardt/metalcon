package de.metalcon.sddServer.error;

public class ServerLevelDbBatchCloseSddError extends SddError {

    private static final long serialVersionUID = -7901990674048384999L;

    @Override
    public String getDescription() {
        return "Closing a LevelDB WriteBatch failed.";
    }

    @Override
    public String getSuggestion() {
        return "This could mean a write to LevelDB failed.\n" +
               "Make sure all data is written, if not reissue the write.";
    }

}
