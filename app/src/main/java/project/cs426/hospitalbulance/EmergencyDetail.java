package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EmergencyDetail extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView ambulanceId, caseType, status, timestamp, addressTextView, callId;
    private Button acceptButton;
    private ImageButton backButton;
    private String callDocumentId;
    private boolean isFromRecord;
    private ListenerRegistration callListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detail);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Link XML elements
        ambulanceId = findViewById(R.id.ambulanceId);
        caseType = findViewById(R.id.caseType);
        status = findViewById(R.id.status);
        timestamp = findViewById(R.id.timestamp);
        addressTextView = findViewById(R.id.address);
        acceptButton = findViewById(R.id.acceptButton);
        backButton = findViewById(R.id.backButton);

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve call ID and isFromRecord flag from intent
        callDocumentId = getIntent().getStringExtra("callId");
        isFromRecord = getIntent().getBooleanExtra("isFromRecord", false);

        // If we are viewing from the record screen, hide the accept button
        if (isFromRecord) {
            acceptButton.setVisibility(Button.GONE);
        }

        // Listen for real-time call data updates
        listenForCallDetails(callDocumentId);

        // Handle accept button click (only if it is visible)
        acceptButton.setOnClickListener(v -> {
            if (currentUser != null) {
                // Ensure the callDocumentId is not null or empty before proceeding
                if (callDocumentId == null || callDocumentId.isEmpty()) {
                    Toast.makeText(EmergencyDetail.this, "Error: Invalid call ID.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call method to update the accepted field and hospital ID
                updateAcceptedAndHospital(callDocumentId);
            } else {
                Toast.makeText(EmergencyDetail.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });
    }

    // Method to listen for real-time updates of the call data
    private void listenForCallDetails(String callDocumentId) {
        if (callDocumentId == null || callDocumentId.isEmpty()) {
            Toast.makeText(this, "Invalid call ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Listen for real-time updates from Firestore using the document ID
        callListener = db.collection("calls")
                .document(callDocumentId)  // Direct document reference by ID
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(EmergencyDetail.this, "Error fetching real-time updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Get the address
                        String address = documentSnapshot.getString("address");
                        addressTextView.setText("Address: " + (address != null ? address : "N/A"));

                        // Get ambulance ID from carID
                        String carID = documentSnapshot.getString("carID");
                        ambulanceId.setText("Ambulance ID: " + (carID != null ? carID : "N/A"));

                        // Get other individual fields
                        caseType.setText("Case: " + (documentSnapshot.getString("case") != null ? documentSnapshot.getString("case") : "N/A"));
                        status.setText("Status: " + (documentSnapshot.getString("status") != null ? documentSnapshot.getString("status") : "N/A"));

                        // Format timestamp
                        Timestamp timestampValue = documentSnapshot.getTimestamp("timestamp");
                        if (timestampValue != null) {
                            Date date = timestampValue.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault());
                            timestamp.setText("Timestamp: " + dateFormat.format(date));
                        } else {
                            timestamp.setText("Timestamp: N/A");
                        }
                    } else {
                        Toast.makeText(EmergencyDetail.this, "No such call exists.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to update "accepted" and "hospitalID" when accepting a case
    private void updateAcceptedAndHospital(String callDocumentId) {
        // Fetch the user's hospital mapID
        String username = currentUser.getEmail();  // Assuming the current user's email is their username
        db.collection("hospitals")
                .whereEqualTo("login-info.username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String mapID = document.getString("mapID");

                        // Check if mapID is valid before proceeding
                        if (mapID == null || mapID.isEmpty()) {
                            Toast.makeText(EmergencyDetail.this, "Error: Invalid hospital ID.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update Firestore to mark the call as accepted and set hospitalID
                        db.collection("calls").document(callDocumentId)
                                .update("accepted", "true", "hospitalID", mapID)
                                .addOnSuccessListener(aVoid -> {
                                    // Send the updated callDocumentId back to the HospitalHomeScreen
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("callId", callDocumentId);  // Pass the call ID back
                                    setResult(RESULT_OK, resultIntent);
                                    finish();  // Close the EmergencyDetail screen
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EmergencyDetail.this, "Failed to accept the emergency case.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EmergencyDetail.this, "Failed to fetch hospital information.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the Firestore listener when the activity is destroyed
        if (callListener != null) {
            callListener.remove();
        }
    }

    private void redirectToLogin() {
        // Redirect the user to the login screen if not authenticated
        Intent loginIntent = new Intent(EmergencyDetail.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
