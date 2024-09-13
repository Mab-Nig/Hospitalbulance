package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.BodyMeasurements;
import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.MedicalInfo;
import project.cs426.hospitalbulance.backend.database.Patient;
import project.cs426.hospitalbulance.backend.database.PatientInfo;

public class EditInfo extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);
        EditText firstName = findViewById(R.id.first_name);

        EditText email = findViewById(R.id.email);
        EditText bloodType = findViewById(R.id.blood_type);
        EditText address = findViewById(R.id.address);
        Button savebtn = findViewById(R.id.save_edit_info);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

// Get Firestore instance
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String username = currentUser.getEmail();
        readDataGeneral(username,firstName,email,bloodType,address);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDataGeneral(username,firstName,email, bloodType, address);
            }
        });
        findViewById(R.id.back_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void readDataGeneral(String username,EditText firstName, EditText email, EditText bloodType, EditText address) {
        this.db.collection(Collections.PATIENTS)
                .whereEqualTo("email",username)
                .get()
                .addOnSuccessListener(querySnapshot-> {
                    for (DocumentSnapshot result : querySnapshot.getDocuments()) {
                        Patient man = result.toObject(Patient.class);
                        MedicalInfo medicalInfo = man.getMedicalInfo();
                        PatientInfo patientInfo = man.getInfo();
                        if(man != null)
                        {
                            email.setText(man.getEmail());
                        }
                        if(patientInfo != null)
                        {
                            firstName.setText(patientInfo.getName());
                            address.setText(patientInfo.getBirthDate().toString());
                        }
                        if (medicalInfo != null) {
                            BodyMeasurements bodyMeasurements = medicalInfo.getBodyMeasurements();
                            if (bodyMeasurements != null) {
                                bloodType.setText(bodyMeasurements.getBloodType());
                            }
                        }
                    }
                });
    }
    private void writeDataGeneral(String username,EditText firstName, EditText email, EditText bloodType, EditText address) {
        this.db.collection(Collections.PATIENTS)
                .whereEqualTo("email", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Extract the document ID
                        String documentId = document.getId();

                        // Create a map to store updated fields
                        Map<String, Object> updates = new HashMap<>();


                        updates.put("medical_info.body_measurements.blood_type", bloodType.getText().toString());



                        updates.put("email", email.getText().toString());
                        updates.put("info.name", firstName.getText().toString());



                        // Perform the update
                        this.db.collection(Collections.PATIENTS)
                                .document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UPDATE DATA FIREBASE", "Patient data has been successfully updated!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UPDATE DATA FIREBASE", "Error updating patient data", e);
                                });
                    }
                });
    }


}
