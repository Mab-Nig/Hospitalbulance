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

public class HospitalRecordScreen extends AppCompatActivity {

    private RecyclerView recordRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> recordList;
    private FirebaseFirestore db;
    private ListenerRegistration recordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_record);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        recordRecyclerView = findViewById(R.id.recordRecyclerView);
        recordList = new ArrayList<>();

        // Listen for records from Firestore where accepted = true
        listenForAcceptedNotifications();

        // Set up adapter with click listener to navigate to EmergencyDetail screen
        adapter = new NotificationAdapter(this, recordList, notification -> {
            Intent intent = new Intent(HospitalRecordScreen.this, EmergencyDetail.class);
            intent.putExtra("callId", notification.getCallId());
            intent.putExtra("isFromRecord", true);
            startActivity(intent);
        });

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordRecyclerView.setAdapter(adapter);

        // Set up the bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String email = getIntent().getStringExtra("username");

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

        bottomNavigationView.setSelectedItemId(R.id.record);
    }

    // Method to listen for real-time updates of records from Firestore where accepted = true
    private void listenForAcceptedNotifications() {
        recordListener = db.collection("calls")
                .whereEqualTo("accepted", "true")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(HospitalRecordScreen.this, "Error fetching real-time records.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordList.clear();  // Clear the list before adding new data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Notification notification = new Notification(
                                document.getString("case") != null ? document.getString("case") : "N/A",
                                document.getString("address") != null ? document.getString("address") : "N/A",
                                document.getString("carID") != null ? document.getString("carID") : "N/A",
                                document.getString("status") != null ? document.getString("status") : "N/A",
                                document.getTimestamp("timestamp") != null ? document.getTimestamp("timestamp").toDate().getTime() : 0,
                                document.getId()  // dispatch ID
                        );
                        recordList.add(notification);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the Firestore listener when the activity is destroyed
        if (recordListener != null) {
            recordListener.remove();
        }
    }
}
