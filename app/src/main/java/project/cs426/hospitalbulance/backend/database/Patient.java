package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;
import com.google.type.Date;

import java.util.List;

public class Patient {
	private PatientInfo info;

	@PropertyName("medical_info")
	private MedicalInfo medicalInfo;

	public Patient() {}

	public Patient(PatientInfo info, MedicalInfo medicalInfo) {
		this.info = info;
		this.medicalInfo = medicalInfo;
	}

	public PatientInfo getInfo() {
		return this.info;
	}

	public MedicalInfo getMedicalInfo() {
		return this.medicalInfo;
	}
}
