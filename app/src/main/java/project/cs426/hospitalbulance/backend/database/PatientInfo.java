package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.Date;

public class PatientInfo {
    private String name = "";
    private Date birthDate = new Date();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("birth_date")
    public Date getBirthDate() {
        return this.birthDate;
    }

    @PropertyName("birth_date")
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
}
