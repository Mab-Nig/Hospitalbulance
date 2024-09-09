package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HospitalRecordScreen extends AppCompatActivity {

    private RecyclerView recordRecyclerView;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_record);

        recordRecyclerView = findViewById(R.id.recordRecyclerView);

        // Use the shared recordList to display the records
        adapter = new NotificationAdapter(this, HospitalHomeScreen.recordList, notification -> {
            Intent intent = new Intent(HospitalRecordScreen.this, EmergencyDetail.class);
            intent.putExtra("dispatchId", notification.getDispatchId());
            intent.putExtra("isFromRecord", true);  // Mark that this is coming from the record screen
            startActivity(intent);
        });

        recordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordRecyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(HospitalRecordScreen.this, HospitalHomeScreen.class));
                return true;
            } else if (itemId == R.id.record) {
                return true;
            }

            return false;
        });
    }
}