package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class OtherMedicalInfo {
    private String alcoholAssumption = "No";
    private boolean isInhalerUsed = false;
    private boolean isDiabetic = false;
    private int fallenCnt = 0;
    private List<String> allergies = new ArrayList<>();
    private List<String> specialAbilities = new ArrayList<>();

    @PropertyName("alcohol_assumption")
    public String getAlcoholAssumption() {
        return this.alcoholAssumption;
    }

    @PropertyName("alcohol_assumption")
    public void setAlcoholAssumption(String level) {
        this.alcoholAssumption = level;
    }

    @PropertyName("is_inhaler_used")
    public boolean isInhalerUsed() {
        return this.isInhalerUsed;
    }

    @PropertyName("is_inhaler_used")
    public void setIsInhalerUsed(boolean inhalerUsed) {
        this.isInhalerUsed = inhalerUsed;
    }

    @PropertyName("is_diabetic")
    public boolean isDiabetic() {
        return this.isDiabetic;
    }

    @PropertyName("is_diabetic")
    public void setDiabetic(boolean diabetic) {
        this.isDiabetic = diabetic;
    }

    @PropertyName("fallen_cnt")
    public int getFallenCnt() {
        return this.fallenCnt;
    }

    @PropertyName("fallen_cnt")
    public void setFallenCnt(int fallenCnt) {
        this.fallenCnt = fallenCnt;
    }

    public List<String> getAllergies() {
        return this.allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    @PropertyName("special_abilities")
    public List<String> getSpecialAbilities() {
        return this.specialAbilities;
    }

    @PropertyName("special_abilities")
    public void setSpecialAbilities(List<String> specialAbilities) {
        this.specialAbilities = specialAbilities;
    }
}
