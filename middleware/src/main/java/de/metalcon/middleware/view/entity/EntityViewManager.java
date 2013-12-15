package de.metalcon.middleware.view.entity;

import de.metalcon.middleware.domain.entity.EntityType;

public abstract class EntityViewManager {
    
    public EntityView createView(EntityType entityType)
    throws Exception {
        switch (entityType) {
            case BAND:   return createBandView();
            case RECORD: return createRecordView();
            case TRACK:  return createTrackView();
            
            default:
                throw new IllegalStateException("Unimplented EntityType: "
                        + entityType.toString() + ".");
        }
    }
    
    public abstract BandView createBandView()
    throws Exception;
    
    public abstract RecordView createRecordView()
    throws Exception;
    
    public abstract TrackView createTrackView()
    throws Exception;

}
