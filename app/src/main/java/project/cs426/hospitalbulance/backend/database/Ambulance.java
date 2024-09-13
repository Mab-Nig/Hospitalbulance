package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Ambulance {
    private String carID="";

    private String carModel ="";
    private String email = "";
    private String mapID = "";
    private boolean isAvailable = true;

    public Ambulance()
    {}

    public Ambulance(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @PropertyName("is_available")
    public boolean isAvailable() {
        return this.isAvailable;
    }

    @PropertyName("is_available")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @PropertyName("car_model")
    public String getCarModel() {
        return carModel;
    }

    @PropertyName("car_model")
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    @PropertyName("car_id")
    public String getCarID() {
        return carID;
    }

    @PropertyName("car_id")
    public void setCarID(String carID) {
        this.carID = carID;
    }

    @PropertyName("maps_id")
    public String getMapID() {
        return mapID;
    }

    @PropertyName("maps_id")
    public void setMapID(String mapID) {
        this.mapID = mapID;
    }

}

