package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class BodyMeasurements {
    private double weight = 0D;
    private double height = 0D;

    private String bloodType ="";

    public double getWeight() {
        return this.weight;
    }

    public BodyMeasurements setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public double getHeight() {
        return this.height;
    }

    public BodyMeasurements setHeight(double height) {
        this.height = height;
        return this;
    }
    @PropertyName("blood_type")
    public String getBloodType() {
        return bloodType;
    }
    @PropertyName("blood_type")
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
}
