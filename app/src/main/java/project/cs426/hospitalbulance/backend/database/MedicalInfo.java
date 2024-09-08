package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Map;

public class MedicalInfo {
	private List<String> symptoms, medications;

	@PropertyName("body_measurements")
	private BodyMeasurements bodyMeasurements;

	private OtherMedicalInfo other;

	public List<String> getSymptoms() {
		return this.symptoms;
	}

	public List<String> getMedications() {
		return this.medications;
	}

	public BodyMeasurements getBodyMeasurements() {
		return this.bodyMeasurements;
	}

	public OtherMedicalInfo getOther() {
		return this.other;
	}
}
