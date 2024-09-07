package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class AmbulanceOwner {
	@PropertyName("maps_id")
	private String mapsId;

	public String getMapsId() {
		return this.mapsId;
	}
}
