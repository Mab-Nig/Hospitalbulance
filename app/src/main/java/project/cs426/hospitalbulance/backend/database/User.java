package project.cs426.hospitalbulance.backend.database;

public class User {
    private String email = "";
    private String role = "";

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }
}
