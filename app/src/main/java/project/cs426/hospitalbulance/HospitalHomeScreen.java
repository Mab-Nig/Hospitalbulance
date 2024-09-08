package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HospitalHomeScreen extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    public static List<Notification> recordList = new ArrayList<>();  // Shared record list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_home);

        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationList = new ArrayList<>();
        notificationList.add(new Notification("Car Accident", "123 Nguyen Van Cu St"));
        notificationList.add(new Notification("Fire Burnt", "456 Hung Vuong St"));

        adapter = new NotificationAdapter(this, notificationList, notification -> {
            Intent intent = new Intent(HospitalHomeScreen.this, EmergencyDetail.class);
            intent.putExtra("notification", notification);
            startActivityForResult(intent, 1);  // Request code 1 for accepting notification
        });

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(adapter);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Notification acceptedNotification = (Notification) data.getSerializableExtra("notification");

            if (acceptedNotification != null) {
                // Remove from home screen list and add to record list
                moveNotificationToRecord(acceptedNotification);
            }
        }
    }

    public void moveNotificationToRecord(Notification notification) {
        // Remove the notification from the home screen list
        notificationList.remove(notification);
        // Add the notification to the record list
        recordList.add(notification);
        // Notify adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }
}