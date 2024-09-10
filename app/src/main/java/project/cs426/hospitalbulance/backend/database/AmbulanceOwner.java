package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class AmbulanceOwner {
    private String mapsId;

    @PropertyName("maps_id")
    public String getMapsId() {
        return this.mapsId;
    }

    @PropertyName("maps_id")
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }
}
