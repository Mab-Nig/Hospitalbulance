package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HospitalEditInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_edit_info);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                startActivity(new Intent(HospitalEditInfo.this, HospitalHomeScreen.class));
                return true;
            } else if (itemId == R.id.record) {
                startActivity(new Intent(HospitalEditInfo.this, HospitalRecordScreen.class));
                return true;
            } else if (itemId == R.id.editinfo) {
                return true;
            }
            return false;
        });
    }
}