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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

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
            intent.putExtra("dispatchId", notification.getDispatchId());
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

    // Method to fetch notifications in real-time from Firestore
    private void listenForNotifications() {
        notificationListener = db.collection("dispatches")
                .whereEqualTo("accepted", "no")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(HospitalHomeScreen.this, "Error fetching real-time updates.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    notificationList.clear();  // Clear the list before adding new data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        // Safely extract fields
                        Map<String, Object> ambulanceInfo = (Map<String, Object>) document.get("ambulance-info");
                        if (ambulanceInfo == null) continue;

                        Notification notification = new Notification(
                                document.getString("case"),
                                document.getString("address"),
                                ambulanceInfo.get("id").toString(),
                                ambulanceInfo.get("owner-id").toString(),
                                document.getString("call-id"),
                                document.getId(),
                                document.getString("hospital-id"),
                                document.getString("status"),
                                document.getTimestamp("timestamp").toDate().getTime()
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
            String dispatchId = data.getStringExtra("dispatchId");

            // Update Firestore to mark the notification as accepted
            db.collection("dispatches").document(dispatchId)
                    .update("accepted", "yes")
                    .addOnSuccessListener(aVoid -> {
                        // No need to manually refresh, real-time listener will handle the updates
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HospitalHomeScreen.this, "Failed to update notification status.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}