package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Call {
    private String callerEmail = "";
    private String status = "";
    private GeoPoint location = new GeoPoint(0.0, 0.0);
    private String carId = "";
    private String hospitalId = "";
    private boolean isAccepted = false;
    private Date timestamp = new Date();

    @PropertyName("caller_email")
    public String getCallerEmail() {
        return callerEmail;
    }

    @PropertyName("caller_email")
    public void setCallerEmail(String callerEmail) {
        this.callerEmail = callerEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("car_id")
    public String getCarId() {
        return carId;
    }

    @PropertyName("car_id")
    public void setCarId(String carId) {
        this.carId = carId;
    }

    @PropertyName("hospital_id")
    public String getHospitalId() {
        return hospitalId;
    }

    @PropertyName("hospital_id")
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName("is_accepted")
    public boolean isAccepted() {
        return isAccepted;
    }

    @PropertyName("is_accepted")
    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
