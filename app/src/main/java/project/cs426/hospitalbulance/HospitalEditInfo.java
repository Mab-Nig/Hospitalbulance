package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class HospitalEditInfo extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText hospitalNameEditText, hospitalAddressEditText;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_info);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        hospitalNameEditText = findViewById(R.id.hospitalNameEditText);
        hospitalAddressEditText = findViewById(R.id.hospitalAddressEditText);
        Button saveInfoButton = findViewById(R.id.saveInfoButton);
        ImageButton logoutButton = findViewById(R.id.logoutButton);
        TextView logoutTextView = findViewById(R.id.logoutTextView);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Get the logged-in user's email from the intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("username");

        // Check if userEmail is valid
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_LONG).show();
            return;
        }

        // Fetch current hospital info from Firestore and display it
        fetchHospitalInfo(userEmail);

        // Handle the save button click
        saveInfoButton.setOnClickListener(v -> saveHospitalInfo(userEmail));

        // Handle logout button click
        logoutButton.setOnClickListener(v -> logout());
        logoutTextView.setOnClickListener(v -> logout());

        // Implement Bottom Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String email = getIntent().getStringExtra("username"); // Get the email from the Intent

            if (itemId == R.id.home) {
                Intent homeIntent = new Intent(HospitalEditInfo.this, HospitalHomeScreen.class);
                homeIntent.putExtra("username", email);
                startActivity(homeIntent);
                return true;
            } else if (itemId == R.id.record) {
                Intent recordIntent = new Intent(HospitalEditInfo.this, HospitalRecordScreen.class);
                recordIntent.putExtra("username", email);
                startActivity(recordIntent);
                return true;
            } else if (itemId == R.id.editinfo) {
                return true;
            }
            return false;
        });
    }

    // Fetch the hospital information based on the email and display it.
    private void fetchHospitalInfo(String email) {
        db.collection("hospitals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Check if the document has the "login-info" field
                                if (document.contains("login-info")) {
                                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");

                                    // Check if the email matches the "username" field in the "login-info"
                                    if (loginInfo != null && email.equals(loginInfo.get("username"))) {
                                        // Fetch the "info" map that contains the hospital details
                                        Map<String, Object> info = (Map<String, Object>) document.get("info");
                                        if (info != null) {
                                            String name = (String) info.get("name");
                                            String address = (String) info.get("address");

                                            Log.d("HospitalEditInfo", "Hospital Name: " + name);
                                            Log.d("HospitalEditInfo", "Hospital Address: " + address);

                                            // Display the hospital name and address in the EditText fields
                                            hospitalNameEditText.setText(name);
                                            hospitalAddressEditText.setText(address);
                                        }
                                        return;
                                    }
                                }
                            }
                            Toast.makeText(HospitalEditInfo.this, "No matching hospital information found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("HospitalEditInfo", "Error getting documents.", task.getException());
                            Toast.makeText(HospitalEditInfo.this, "Failed to retrieve hospital info.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Save the updated hospital info back to Firestore.
    private void saveHospitalInfo(String email) {
        String updatedName = hospitalNameEditText.getText().toString().trim();
        String updatedAddress = hospitalAddressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName)) {
            hospitalNameEditText.setError("Hospital name is required.");
            hospitalNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(updatedAddress)) {
            hospitalAddressEditText.setError("Address is required.");
            hospitalAddressEditText.requestFocus();
            return;
        }

        db.collection("hospitals")
                .whereEqualTo("login-info.username", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assuming we only have one matching hospital document, get the first result
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();

                        // Update the "info" map with the new name and address
                        db.collection("hospitals").document(documentId)
                                .update("info.name", updatedName, "info.address", updatedAddress)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(HospitalEditInfo.this, "Information updated successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HospitalEditInfo.this, "Failed to update information.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(HospitalEditInfo.this, "No matching hospital found to update.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Logout the user
    private void logout() {
        mAuth.signOut();
        Intent logoutIntent = new Intent(HospitalEditInfo.this, LoginActivity.class);
        startActivity(logoutIntent);
        finish();
    }
}