package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Hospital {
    private PlaceInfo info = new PlaceInfo();
    private String email = "";
    private String name = "";
    private String mapsId = "";

    public Hospital(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public PlaceInfo getInfo() {
        return info;
    }

    public void setInfo(PlaceInfo info) {
        this.info = info;
    }
}
