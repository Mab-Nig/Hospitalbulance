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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        // Listen for records from Firestore where accepted = "yes"
        listenForAcceptedNotifications();

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

    // Method to listen for real-time updates of records from Firestore where accepted = "yes"
    private void listenForAcceptedNotifications() {
        recordListener = db.collection("dispatches")
                .whereEqualTo("accepted", "yes")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(HospitalRecordScreen.this, "Error fetching real-time records.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    recordList.clear();  // Clear the list before adding new data
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
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