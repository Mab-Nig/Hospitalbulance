package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;
public class Call {
    private String patientId = "";
    private String ambulanceId = "";
    private String hospitalId = "";

    public Call() {}

    public Call(String patientId) {
        this.patientId = patientId;
    }

    @PropertyName("patient_id")
    public String getPatientId() {
        return patientId;
    }

    @PropertyName("patient_id")
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @PropertyName("ambulance_id")
    public String getAmbulanceId() {
        return ambulanceId;
    }

    @PropertyName("ambulance_id")
    public void setAmbulanceId(String ambulanceId) {
        this.ambulanceId = ambulanceId;
    }

    @PropertyName("hospital_id")
    public String getHospitalId() {
        return hospitalId;
    }

    @PropertyName("hospital_id")
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
}
