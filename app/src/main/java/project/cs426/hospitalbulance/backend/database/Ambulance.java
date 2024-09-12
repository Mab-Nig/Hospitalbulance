package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Ambulance {

    private String carModel ="";
    private String email = "";
    private CarInfo carInfo = new CarInfo();
    private boolean isAvailable = true;

    public Ambulance(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("car_info")
    public CarInfo getCarInfo() {
        return this.carInfo;
    }

    @PropertyName("car_info")
    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
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
}
