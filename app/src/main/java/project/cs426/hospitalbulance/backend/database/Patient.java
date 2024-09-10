package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Patient {
    private PatientInfo info = new PatientInfo();
    private MedicalInfo medicalInfo = new MedicalInfo();

    public PatientInfo getInfo() {
        return this.info;
    }

    public void setInfo(PatientInfo info) {
        this.info = info;
    }

    @PropertyName("medical_info")
    public MedicalInfo getMedicalInfo() {
        return this.medicalInfo;
    }

    @PropertyName("medical_info")
    public void setMedicalInfo(MedicalInfo medicalInfo) {
        this.medicalInfo = medicalInfo;
    }
}
