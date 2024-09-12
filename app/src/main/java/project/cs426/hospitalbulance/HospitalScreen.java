package project.cs426.hospitalbulance;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HospitalScreen extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_screen);

        fragmentManager = getSupportFragmentManager();

        // Load the default fragment (HospitalHomeFragment) on launch
        loadFragment(new HospitalHomeFragment());

        // BottomNavigationView for switching between fragments
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                selectedFragment = new HospitalHomeFragment();
            } else if (itemId == R.id.record) {
                selectedFragment = new HospitalRecordFragment();
            } else if (itemId == R.id.editinfo) {
                HospitalEditInfoFragment infoFragment = new HospitalEditInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username", getIntent().getStringExtra("username"));  // Pass the username to the fragment
                infoFragment.setArguments(bundle);
                selectedFragment = infoFragment;
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
