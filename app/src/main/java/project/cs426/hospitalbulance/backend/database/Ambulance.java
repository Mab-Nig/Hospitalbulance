package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Ambulance {
    private String number;
    private boolean isAvailable;

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @PropertyName("is_available")
    public boolean isAvailable() {
        return this.isAvailable;
    }

    @PropertyName("is_available")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
