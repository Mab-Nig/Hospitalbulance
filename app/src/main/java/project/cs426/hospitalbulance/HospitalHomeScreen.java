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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HospitalHomeScreen extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    public static List<Notification> recordList = new ArrayList<>();  // Shared record list
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationList = new ArrayList<>();

        // Fetch notifications from Firestore (replace this with actual Firestore query)
        fetchNotifications();

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

            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.record) {
                startActivity(new Intent(HospitalHomeScreen.this, HospitalRecordScreen.class));
                return true;
            }

            return false;
        });
    }

    // Method to fetch notifications from Firestore
    private void fetchNotifications() {
        db.collection("dispatches")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notificationList.clear();  // Clear the list before adding new data
                        for (DocumentSnapshot document : task.getResult()) {
                            Notification notification = new Notification(
                                    document.getString("case"),
                                    document.getString("address"),
                                    ((Map<String, Object>) document.get("ambulance-info")).get("id").toString(),
                                    ((Map<String, Object>) document.get("ambulance-info")).get("owner-id").toString(),
                                    document.getString("call-id"),
                                    document.getString("dispatchID"),
                                    document.getString("hospital-id"),
                                    document.getString("status"),
                                    document.getTimestamp("timestamp").toDate().getTime()
                            );
                            notificationList.add(notification);
                        }
                        adapter.notifyDataSetChanged();  // Update the RecyclerView
                    } else {
                        Toast.makeText(HospitalHomeScreen.this, "Failed to fetch notifications.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String dispatchId = data.getStringExtra("dispatchId");

            // Find the accepted notification and move it to the record screen
            Notification acceptedNotification = null;
            for (Notification notification : notificationList) {
                if (notification.getDispatchId().equals(dispatchId)) {
                    acceptedNotification = notification;
                    break;
                }
            }

            if (acceptedNotification != null) {
                moveNotificationToRecord(acceptedNotification);
            }
        }
    }

    public void moveNotificationToRecord(Notification notification) {
        // Remove the notification from the home screen list
        notificationList.remove(notification);
        // Add the notification to the record list
        recordList.add(notification);
        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }
}