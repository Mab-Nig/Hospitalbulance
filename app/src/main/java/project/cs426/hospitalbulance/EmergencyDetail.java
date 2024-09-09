package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EmergencyDetail extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView ambulanceId, ownerId, callId, caseType, hospitalId, status, timestamp, addressTextView, dispatchId;
    private Button acceptButton;
    private ImageButton backButton;
    private String dispatchDocumentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detail);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

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

        // Retrieve dispatch ID from intent or other sources
        dispatchDocumentId = getIntent().getStringExtra("dispatchId");

        // Fetch dispatch data from Firestore
        fetchDispatchDetails(dispatchDocumentId);

        // Handle accept button click
        acceptButton.setOnClickListener(view -> {
            // Handle the accept action here, e.g., updating the status and finishing the activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("dispatchId", dispatchDocumentId);
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    private void fetchDispatchDetails(String dispatchDocumentId) {
        if (dispatchDocumentId == null || dispatchDocumentId.isEmpty()) {
            Toast.makeText(this, "Invalid dispatch ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query Firestore using the dispatchDocumentId
        db.collection("dispatches")
                .whereEqualTo("dispatchID", dispatchDocumentId) // Fetch the document with the correct dispatch ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Loop through results (should be one match)
                        for (DocumentSnapshot document : task.getResult()) {
                            // Get the address
                            String address = document.getString("address");
                            addressTextView.setText("Address: " + address);

                            // Get ambulance-info (map with id and owner-id)
                            Map<String, Object> ambulanceInfo = (Map<String, Object>) document.get("ambulance-info");
                            if (ambulanceInfo != null) {
                                ambulanceId.setText("Ambulance ID: " + ambulanceInfo.get("id"));
                                ownerId.setText("Owner ID: " + ambulanceInfo.get("owner-id"));
                            }

                            // Get other individual fields
                            callId.setText("Call ID: " + document.getString("call-id"));
                            caseType.setText("Case: " + document.getString("case"));
                            hospitalId.setText("Hospital ID: " + document.getString("hospital-id"));
                            status.setText("Status: " + document.getString("status"));

                            // Set the dispatch ID (TextView)
                            dispatchId.setText("Dispatch ID: " + document.getString("dispatchID"));

                            // Format timestamp
                            Timestamp timestampValue = document.getTimestamp("timestamp");
                            if (timestampValue != null) {
                                Date date = timestampValue.toDate();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' h:mm a", Locale.getDefault());
                                timestamp.setText("Timestamp: " + dateFormat.format(date));
                            }
                        }
                    } else {
                        Toast.makeText(EmergencyDetail.this, "No such dispatch exists.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(EmergencyDetail.this, "Failed to retrieve dispatch details.", Toast.LENGTH_SHORT).show();
                });
    }
}