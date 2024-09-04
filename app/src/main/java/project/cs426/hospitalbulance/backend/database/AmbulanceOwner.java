package project.cs426.hospitalbulance.backend.database;

import java.util.List;

public class AmbulanceOwner {
	private String mapsId;
	private List<Ambulance> ambulances;

	public String getMapsId() {
		return this.mapsId;
	}
}
