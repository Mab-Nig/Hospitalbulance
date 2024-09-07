package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class PatientInfo {
	private String name;

	@PropertyName("birth_date")
	private Date birthDate;

	public String getName() {
		return this.name;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}
}
