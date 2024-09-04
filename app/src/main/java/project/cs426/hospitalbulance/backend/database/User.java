package project.cs426.hospitalbulance.backend.database;

public class User {
	private String role;

	public User() {}

	public User(String role) {
		this.role = role;
	}

	public String getRole() {
		return this.role;
	}
}
