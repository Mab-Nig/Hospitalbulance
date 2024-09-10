package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Hospital {
    private String name;
    private String mapsId;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("maps_id")
    public String getMapsId() {
        return this.mapsId;
    }

    @PropertyName("maps_id")
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }
}
