package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class Call {
    private String address ="";
    private String cases ="";
    private String callerEmail = "";
    private String status = "";
    private GeoPoint location = new GeoPoint(0.0, 0.0);
    private String carId = "";
    private String hospitalId = "";
    private boolean isAccepted = false;
    private Date timestamp = new Date();

    private int children = 0;

    private int adults = 0;

    private String mapsId = "";

    private String process = "waiting";

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   @PropertyName("case")
    public String getCases() {
        return cases;
    }
@PropertyName("case")
    public void setCases(String cases) {
        this.cases = cases;
    }

    @PropertyName("maps_id")
    public String getMapsId() {
        return mapsId;
    }

    @PropertyName("maps_id")
    public void setMapsId(String maps_id) {
        this.mapsId = maps_id;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }
}
