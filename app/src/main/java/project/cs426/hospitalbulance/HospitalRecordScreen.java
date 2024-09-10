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

public class HospitalRecordScreen extends AppCompatActivity {

    private RecyclerView recordRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> recordList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_record);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        recordRecyclerView = findViewById(R.id.recordRecyclerView);
        recordList = new ArrayList<>();

        // Fetch records from Firestore where accepted = "yes"
        fetchAcceptedNotifications();

        // Set up adapter with click listener to navigate to EmergencyDetail screen
        adapter = new NotificationAdapter(this, recordList, notification -> {
            Intent intent = new Intent(HospitalRecordScreen.this, EmergencyDetail.class);
            intent.putExtra("dispatchId", notification.getDispatchId());
            intent.putExtra("isFromRecord", true);
            startActivity(intent);
        });

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordRecyclerView.setAdapter(adapter);

        // Set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String email = getIntent().getStringExtra("username"); // Get the email from the Intent

            if (itemId == R.id.home) {
                Intent recordIntent = new Intent(HospitalRecordScreen.this, HospitalHomeScreen.class);
                recordIntent.putExtra("username", email);
                startActivity(recordIntent);
                return true;
            } else if (itemId == R.id.record) {
                return true;
            } else if (itemId == R.id.editinfo) {
                Intent editInfoIntent = new Intent(HospitalRecordScreen.this, HospitalEditInfo.class);
                editInfoIntent.putExtra("username", email);
                startActivity(editInfoIntent);
                return true;
            }
            return false;
        });

    }

    // Method to fetch records from Firestore where accepted = "yes"
    private void fetchAcceptedNotifications() {
        db.collection("dispatches")
                .whereEqualTo("accepted", "yes")  // Fetch records where accepted = "yes"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recordList.clear();  // Clear the list before adding new data
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> ambulanceInfo = (Map<String, Object>) document.get("ambulance-info");
                            if (ambulanceInfo == null) continue;

                            Notification notification = new Notification(
                                    document.getString("case"),
                                    document.getString("address"),
                                    ambulanceInfo.get("id").toString(),
                                    ambulanceInfo.get("owner-id").toString(),
                                    document.getString("call-id"),
                                    document.getId(),  // Use document's unique Firestore ID here
                                    document.getString("hospital-id"),
                                    document.getString("status"),
                                    document.getTimestamp("timestamp").toDate().getTime()
                            );
                            recordList.add(notification);
                        }
                        adapter.notifyDataSetChanged();  // Update the RecyclerView
                    } else {
                        Toast.makeText(HospitalRecordScreen.this, "Failed to fetch records.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HospitalRecordScreen.this, "Error fetching records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}