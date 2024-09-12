package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HospitalHomeScreen extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private FirebaseFirestore db;
    private ListenerRegistration notificationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationList = new ArrayList<>();

        // Fetch notifications from Firestore in real-time
        listenForNotifications();

        adapter = new NotificationAdapter(this, notificationList, notification -> {
            // Navigate to EmergencyDetail screen with the selected notification's dispatch ID
            Intent intent = new Intent(HospitalHomeScreen.this, EmergencyDetail.class);
            intent.putExtra("callId", notification.getCallId());
            startActivityForResult(intent, 1);  // Request code 1 for accepting notification
        });

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(adapter);

        // Set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String email = getIntent().getStringExtra("username"); // Get the email from the Intent

            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.record) {
                Intent recordIntent = new Intent(HospitalHomeScreen.this, HospitalRecordScreen.class);
                recordIntent.putExtra("username", email);
                startActivity(recordIntent);
                return true;
            } else if (itemId == R.id.editinfo) {
                Intent editInfoIntent = new Intent(HospitalHomeScreen.this, HospitalEditInfo.class);
                editInfoIntent.putExtra("username", email);
                startActivity(editInfoIntent);
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    // Method to fetch notifications in real-time from Firestore "calls" collection where accepted = false
    private void listenForNotifications() {
        notificationListener = db.collection("calls")
                .whereEqualTo("accepted", "false")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(HospitalHomeScreen.this, "Error fetching real-time updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    notificationList.clear();  // Clear the list before adding new data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Safely extract fields
                        Notification notification = new Notification(
                                document.getString("case") != null ? document.getString("case") : "N/A",
                                document.getString("address") != null ? document.getString("address") : "N/A",
                                document.getString("carID") != null ? document.getString("carID") : "N/A",
                                document.getString("status") != null ? document.getString("status") : "N/A",
                                document.getTimestamp("timestamp") != null ? document.getTimestamp("timestamp").toDate().getTime() : 0,
                                document.getId()  // dispatch ID
                        );
                        notificationList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the Firestore listener when the activity is destroyed
        if (notificationListener != null) {
            notificationListener.remove();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String callId = data.getStringExtra("callId");

            // Check if callId is null before proceeding
            if (callId == null || callId.isEmpty()) {
                Toast.makeText(this, "Error: Invalid call ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch the user's hospital mapID
            String username = getIntent().getStringExtra("username");
            db.collection("hospitals")
                    .whereEqualTo("login-info.username", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String mapID = document.getString("mapID");

                            // Check if mapID is null before updating Firestore
                            if (mapID == null || mapID.isEmpty()) {
                                Toast.makeText(HospitalHomeScreen.this, "Error: Invalid hospital ID.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Update Firestore to mark the call as accepted and set hospitalID
                            db.collection("calls").document(callId)
                                    .update("accepted", "true", "hospitalID", mapID)
                                    .addOnSuccessListener(aVoid -> {
                                        // No need to manually refresh, real-time listener will handle the updates
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(HospitalHomeScreen.this, "Failed to update notification status.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
        }
    }
}
