package project.cs426.hospitalbulance.backend.database;

import java.util.List;
import java.util.Map;

public class MedicalInfo {
	private List<String> symptoms, medications;
	private Map<String, Object> bodyMeasurements, other;

	public List<String> getSymptoms() {
		return this.symptoms;
	}

	public List<String> getMedications() {
		return this.medications;
	}

	public Map<String, Object> getBodyMeasurements() {
		return this.bodyMeasurements;
	}

	public Map<String, Object> getOther() {
		return this.other;
	}
}
