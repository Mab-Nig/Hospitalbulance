package project.cs426.hospitalbulance.backend.database;

public class User {
	private String mRole;

	public User() {}

	public User(String role) {
		mRole = role;
	}

	public String getRole() {
		return mRole;
	}
}
