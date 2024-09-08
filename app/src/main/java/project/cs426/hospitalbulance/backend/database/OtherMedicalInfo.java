package project.cs426.hospitalbulance.backend.database;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class OtherMedicalInfo {
	@PropertyName("alcohol_assumption")
	private String alcoholAssumption;

	@PropertyName("is_inhaler_used")
	private boolean isInhalerUsed;

	@PropertyName("is_diabetic")
	private boolean isDiabetic;

	@PropertyName("fallen_cnt")
	private int fallenCnt;

	private List<String> allergies;

	@PropertyName("special_abilities")
	private List<String> specialAbilities;

	public String getAlcoholAssumption() {
		return this.alcoholAssumption;
	}

	public boolean isInhalerUsed() {
		return this.isInhalerUsed;
	}

	public boolean isDiabetic() {
		return this.isDiabetic;
	}

	public int getFallenCnt() {
		return this.fallenCnt;
	}

	public List<String> getAllergies() {
		return this.allergies;
	}

	public List<String> getSpecialAbilities() {
		return this.specialAbilities;
	}
}
