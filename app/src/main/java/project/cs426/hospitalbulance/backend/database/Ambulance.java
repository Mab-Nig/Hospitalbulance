package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Ambulance {
	private String number;

	@PropertyName("is_available")
	private boolean isAvailable;

	public String getNumber() {
		return this.number;
	}

	public boolean isAvailable() {
		return this.isAvailable;
	}
}
