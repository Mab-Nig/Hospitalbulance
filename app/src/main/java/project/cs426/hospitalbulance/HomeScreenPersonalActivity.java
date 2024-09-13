package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.BodyMeasurements;
import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.MedicalInfo;
import project.cs426.hospitalbulance.backend.database.OtherMedicalInfo;
import project.cs426.hospitalbulance.backend.database.Patient;

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


        prepareContext();
        findViewById(R.id.sidebar_image3).setOnClickListener(v -> showLogoutConfirmationDialog());
        findViewById(R.id.sidebar_image1).setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenPersonalActivity.this, EditInfo.class);

            startActivity(intent);
        });

        addSignOutListener();
    }

    public void logoutClicked() {
        FirebaseAuth.getInstance().signOut();
    }

    private void addSignOutListener() {
        FirebaseAuth.IdTokenListener signOutListener = auth -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(HomeScreenPersonalActivity.this, SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        };
        FirebaseAuth.getInstance().addIdTokenListener(signOutListener);
    }
    
    private void prepareContext() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView blood = findViewById(R.id.blood);
        TextView weight = findViewById(R.id.weight);
        TextView diabetic = findViewById(R.id.diabetic);
        TextView allergies = findViewById(R.id.allergies);
        Log.d("READ DATA FIREBASE",currentUser.getEmail());
        readData(currentUser.getEmail(), blood, weight, diabetic, allergies);
        //readData(username, blood, weight, diabetic, allergies);
    }


    private void readData(String username, TextView blood, TextView weight, TextView diabetic, TextView allergies) {

            this.db.collection(Collections.PATIENTS)
                    .whereEqualTo("email",username)
                    .addSnapshotListener((querySnapshot,e)->{
                        if(e != null)
                        {
                            Log.e("READ DATA FIREBASE", "Error getting documents: ", e);
                            return;
                        }
                        for( DocumentSnapshot result : querySnapshot.getDocuments())
                        {
                            Patient man = result.toObject(Patient.class);
                            Log.d("READ DATA FIREBASE", "readMedications: " + result.getData());
                            MedicalInfo medicalInfo = man.getMedicalInfo();

                            if (medicalInfo != null) {
                                OtherMedicalInfo otherMedicalInfo = medicalInfo.getOther();
                                if(otherMedicalInfo != null){
                                    String diabetic_result = (otherMedicalInfo.isDiabetic() ? "Yes" : "No");
                                    diabetic.setText("Diabetic - " + diabetic_result);
                                    Number fallen = (Number) otherMedicalInfo.getFallenCnt();
                                    if(fallen.intValue() > 0) {
                                        allergies.setText("Allergies - " +String.valueOf(fallen));
                                    } else {
                                        allergies.setText("Allergies - None");
                                    }
                                }

                                BodyMeasurements bodyMeasurements = medicalInfo.getBodyMeasurements();
                                if (bodyMeasurements != null) {
                                    blood.setText("Blood Group - " + bodyMeasurements.getBloodType());
                                    weight.setText("Weight - " + String.valueOf(bodyMeasurements.getWeight()) + " kgs");
                                }

                            }
                        }
                    });

        }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenPersonalActivity.this);
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, id) -> {

                    logoutClicked();

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    


    private void saveCredentials1(String email) {

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.putString("email", email);
        editor.putString("password","123456");
        editor.apply(); // Save new data

    }
}
