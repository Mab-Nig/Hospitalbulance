package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

public class Ambulance {
    private boolean isAvailable;

    @PropertyName("is_available")
    public boolean isAvailable() {
        return this.isAvailable;
    }

    @PropertyName("is_available")
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
