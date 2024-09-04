package project.cs426.hospitalbulance.backend.database;

import com.google.type.Date;

import java.util.List;

public class Patient {
	private PatientInfo info;
	private MedicalInfo medicalInfo;

	public PatientInfo getInfo() {
		return this.info;
	}

	public MedicalInfo getMedicalInfo() {
		return this.medicalInfo;
	}
}
