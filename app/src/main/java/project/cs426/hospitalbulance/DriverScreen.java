package project.cs426.hospitalbulance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriverScreen extends AppCompatActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_screen);

        fragmentManager = getSupportFragmentManager();

        Fragment defaultHomeScreen = new DriverHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", getIntent().getStringExtra("username"));  // Pass the username to the fragment
        defaultHomeScreen.setArguments(bundle);
        // Load the default fragment (HospitalHomeFragment) on launch
        loadFragment(defaultHomeScreen);

        // BottomNavigationView for switching between fragments
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = defaultHomeScreen;
            } else if (itemId == R.id.record) {
                DriverRecordFragment recordFragment = new DriverRecordFragment();
                bundle.putString("username", getIntent().getStringExtra("username"));  // Pass the username to the fragment
                recordFragment.setArguments(bundle);
                selectedFragment = recordFragment;
            } else if (itemId == R.id.editinfo) {
                selectedFragment = new DriverPersonalFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
