package project.cs426.hospitalbulance;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class HomeScreenPersonalActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_personal);

        drawerLayout = findViewById(R.id.drawer_layout);
        RelativeLayout navigationView = findViewById(R.id.sidebar_layout);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

// Get Firestore instance
        db = FirebaseFirestore.getInstance();

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        findViewById(R.id.sidebar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        findViewById(R.id.back_sidebar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(navigationView);
            }
        });
        findViewById(R.id.sidebar_image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreenPersonalActivity.this, PaymentActivity.class);

                startActivity(intent);
            }
        });
        prepareContext("user@gmail.com");
        ImageButton logoutButton = findViewById(R.id.sidebar_image3);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void prepareContext(String username) {
        TextView blood = findViewById(R.id.blood);
        TextView weight = findViewById(R.id.weight);
        TextView diabetic = findViewById(R.id.diabetic);
        TextView allergies = findViewById(R.id.allergies);
        readData(username, blood, weight, diabetic, allergies);
    }

    private void readData(String username, TextView blood, TextView weight, TextView diabetic, TextView allergies) {

        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", ":InsideReadData");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the 'general-info' map
                    Map<String, Object> medicalInfo = (Map<String, Object>) document.get("medical-info");
                    // Access the 'login-info' map
                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");
                    Map<String, Object> generalInfo = (Map<String, Object>) document.get("general-info");

                    if (loginInfo != null) {
                        // Example: Access fields in 'login-info' map
                        String username_get = (String) loginInfo.get("username");
                        // Check and use the data
                        if (username_get != null) {
                            Log.d("Firestore", "User ID: " + document.getId() +
                                    " Username: " + username_get);
                            if (username_get.equals(username)) {
                                if (medicalInfo != null) {

                                    Number fallen = (Number) medicalInfo.get("fallen-cnt");
                                    boolean diabeticType = (boolean) medicalInfo.get("is-diabetic");
                                    if(diabeticType)
                                    {
                                        diabetic.setText("Diabetic - Yes");
                                    }
                                    else
                                    {
                                        diabetic.setText("Diabetic - No");
                                    }

                                    if(fallen.intValue() > 0)
                                    {
                                        allergies.setText("Allergies - " +String.valueOf(fallen));
                                    }
                                    else
                                    {
                                        allergies.setText("Allergies - None");
                                    }
                                }
                                if (generalInfo != null) {
                                    String bloodType = (String) generalInfo.get("blood-type");
                                    blood.setText("Blood Group - " + bloodType + " +ve");
                                    Log.d("Blood", "Success" + bloodType);
                                    Number weightNum = (Number) generalInfo.get("weight");
                                    weight.setText("Weight - " + String.valueOf(weightNum) + " kgs");
                                    Log.d("Weight", "Success" + weightNum);
                                }


                            }
                        }
                    }
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenPersonalActivity.this);
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, id) -> {
                    Intent intent = new Intent(HomeScreenPersonalActivity.this,SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
