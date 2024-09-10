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
    private TextView ambulanceId, ownerId, callId, caseType, hospitalId, status, timestamp, addressTextView, dispatchId;
    private Button acceptButton;
    private ImageButton backButton;
    private String dispatchDocumentId;
    private boolean isFromRecord;
    private ListenerRegistration dispatchListener;

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
        ownerId = findViewById(R.id.ownerId);
        callId = findViewById(R.id.callId);
        caseType = findViewById(R.id.caseType);
        hospitalId = findViewById(R.id.hospitalId);
        status = findViewById(R.id.status);
        timestamp = findViewById(R.id.timestamp);
        dispatchId = findViewById(R.id.dispatchId);
        addressTextView = findViewById(R.id.address);
        acceptButton = findViewById(R.id.acceptButton);
        backButton = findViewById(R.id.backButton);

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve dispatch ID and isFromRecord flag from intent
        dispatchDocumentId = getIntent().getStringExtra("dispatchId");
        isFromRecord = getIntent().getBooleanExtra("isFromRecord", false);

        // If we are viewing from the record screen, hide the accept button
        if (isFromRecord) {
            acceptButton.setVisibility(Button.GONE);
        }

        // Listen for real-time dispatch data updates
        listenForDispatchDetails(dispatchDocumentId);

        // Handle accept button click (only if it is visible)
        acceptButton.setOnClickListener(v -> {
            if (currentUser != null) {
                // Update the "accepted" field to "yes" using document's unique ID
                db.collection("dispatches").document(dispatchDocumentId)
                        .update("accepted", "yes")
                        .addOnSuccessListener(aVoid -> {
                            // Return the dispatch ID to the HospitalHomeScreen
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("dispatchId", dispatchDocumentId);
                            setResult(RESULT_OK, resultIntent);
                            finish();  // Close the EmergencyDetail screen
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EmergencyDetail.this, "Failed to accept the emergency case.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(EmergencyDetail.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });
    }

    // Method to listen for real-time updates of the dispatch data
    private void listenForDispatchDetails(String dispatchDocumentId) {
        if (dispatchDocumentId == null || dispatchDocumentId.isEmpty()) {
            Toast.makeText(this, "Invalid dispatch ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Listen for real-time updates from Firestore using the document ID
        dispatchListener = db.collection("dispatches")
                .document(dispatchDocumentId)  // Direct document reference by ID
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(EmergencyDetail.this, "Error fetching real-time updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Get the address
                        String address = documentSnapshot.getString("address");
                        addressTextView.setText("Address: " + address);

                        // Get ambulance-info (map with id and owner-id)
                        Map<String, Object> ambulanceInfo = (Map<String, Object>) documentSnapshot.get("ambulance-info");
                        if (ambulanceInfo != null) {
                            ambulanceId.setText("Ambulance ID: " + ambulanceInfo.get("id"));
                            ownerId.setText("Owner ID: " + ambulanceInfo.get("owner-id"));
                        }

                        // Get other individual fields
                        callId.setText("Call ID: " + documentSnapshot.getString("call-id"));
                        caseType.setText("Case: " + documentSnapshot.getString("case"));
                        hospitalId.setText("Hospital ID: " + documentSnapshot.getString("hospital-id"));
                        status.setText("Status: " + documentSnapshot.getString("status"));

                        // Set the dispatch ID (TextView)
                        dispatchId.setText("Dispatch ID: " + documentSnapshot.getString("dispatchID"));

                        // Format timestamp
                        Timestamp timestampValue = documentSnapshot.getTimestamp("timestamp");
                        if (timestampValue != null) {
                            Date date = timestampValue.toDate();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault());
                            timestamp.setText("Timestamp: " + dateFormat.format(date));
                        }
                    } else {
                        Toast.makeText(EmergencyDetail.this, "No such dispatch exists.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the Firestore listener when the activity is destroyed
        if (dispatchListener != null) {
            dispatchListener.remove();
        }
    }

    private void redirectToLogin() {
        // Redirect the user to the login screen if not authenticated
        Intent loginIntent = new Intent(EmergencyDetail.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}