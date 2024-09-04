package project.cs426.hospitalbulance.backend.database;

import com.google.type.Date;

public class PatientInfo {
	private String name;
	private Date birthDate;

	public String getName() {
		return this.name;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}
}
