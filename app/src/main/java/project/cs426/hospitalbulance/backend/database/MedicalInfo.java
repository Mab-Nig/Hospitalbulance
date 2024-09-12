package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class MedicalInfo {
    private List<String> symptoms = new ArrayList<>();
    private List<String> medications = new ArrayList<>();
    private BodyMeasurements bodyMeasurements = new BodyMeasurements();
    private OtherMedicalInfo other = new OtherMedicalInfo();

    public List<String> getSymptoms() {
        return this.symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public List<String> getMedications() {
        return this.medications;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    @PropertyName("body_measurements")
    public BodyMeasurements getBodyMeasurements() {
        return this.bodyMeasurements;
    }

    @PropertyName("body_measurements")
    public void setBodyMeasurements(BodyMeasurements bodyMeasurements) {
        this.bodyMeasurements = bodyMeasurements;
    }

    public OtherMedicalInfo getOther() {
        return this.other;
    }

    public void setOther(OtherMedicalInfo other) {
        this.other = other;
    }
}
