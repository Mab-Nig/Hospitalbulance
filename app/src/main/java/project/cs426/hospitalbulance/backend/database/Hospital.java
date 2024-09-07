package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Hospital {
	private String name;

	@PropertyName("maps_id")
	private String mapsId;

	public String getName() {
		return this.name;
	}

	public String getMapsId() {
		return this.mapsId;
	}
}
