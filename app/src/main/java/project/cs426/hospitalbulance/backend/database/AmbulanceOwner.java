package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class AmbulanceOwner {

    private String email = "";
    private String mapsId = "";

    private PlaceInfo info = new PlaceInfo();

    public AmbulanceOwner(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("maps_id")
    public String getMapsId() {
        return this.mapsId;
    }

    @PropertyName("maps_id")
    public void setMapsId(String mapsId) {
        this.mapsId = mapsId;
    }

    public PlaceInfo getInfo() {
        return info;
    }

    public void setInfo(PlaceInfo info) {
        this.info = info;
    }
}
