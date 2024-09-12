package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class AmbulancePersonal extends AppCompatActivity {
    private Switch switchShowAddress;
    private ScrollView scrollViewAddress;
    private FirebaseFirestore db;
    private EditText etCarIdPer;
    private EditText etCarModelPer;
    private TextView tvCarAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_personal);
        // Find views by their ID
        switchShowAddress = findViewById(R.id.switch_show_address);
        scrollViewAddress = findViewById(R.id.scroll_view_address);
        etCarIdPer = findViewById(R.id.et_car_id_per);
        etCarModelPer = findViewById(R.id.et_car_model_per);
        tvCarAddress = findViewById(R.id.tv_car_address);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

         // Get Firestore instance
        db = FirebaseFirestore.getInstance();
        // Set listener for the switch
        Intent intent_sup = getIntent();
        String username = intent_sup.getStringExtra("username");
        readData(username,etCarIdPer,etCarModelPer,switchShowAddress,scrollViewAddress,tvCarAddress);
        findViewById(R.id.btn_logout_am).setOnClickListener(v -> showLogoutConfirmationDialog(username));
        switchShowAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the address when switch is ON
                scrollViewAddress.setVisibility(ScrollView.VISIBLE);
            } else {
                // Hide the address when switch is OFF
                scrollViewAddress.setVisibility(ScrollView.GONE);
            }
        });




    }
    private void readData(String username, EditText etCarIdPer, EditText etCarModelPer,Switch switchShowAddress,ScrollView scrollViewAddress,TextView tvCarAddress) {
        CollectionReference ambulancesRef = db.collection("ambulances");
        ambulancesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", ":InsideReadData");
                for (QueryDocumentSnapshot document : task.getResult()) {

                   String carIdInfo =(String) document.get("carID");

                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");
                    String carModelInfo =(String) document.get("car_model");
                    Boolean isAvailable = (Boolean) document.get("available");
                    String mapID = (String) document.get("mapID");
                    if (loginInfo != null) {
                        // Example: Access fields in 'login-info' map
                        String username_get = (String) loginInfo.get("username");
                        // Check and use the data
                        if (username_get != null) {
                            Log.d("Firestore", "User ID: " + document.getId() +
                                    " Username: " + username_get);
                            if (username_get.equals(username)) {
                                 etCarIdPer.setText(carIdInfo);
                                 etCarModelPer.setText(carModelInfo);
                                Log.d("Ambulance",carIdInfo);
                                Log.d("Ambulance",carModelInfo);
                                switchShowAddress.setChecked(isAvailable);
                                if (isAvailable) {
                                    Log.d("Firebase_mapID1",mapID);
                                    showAddress(mapID,tvCarAddress);
                                    scrollViewAddress.setVisibility(ScrollView.VISIBLE);

                                } else {
                                    scrollViewAddress.setVisibility(ScrollView.GONE);
                                }
                                 break;
                            }
                        }
                    }
                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
    }
    private void showAddress(String mapID,TextView tvCarAddress)
    {
        CollectionReference ambulance_owners = db.collection("ambulance_owners");
        ambulance_owners.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", ":InsideReadData_showAddress");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> Info = (Map<String, Object>) document.get("info");
                    String address_get = (String) Info.get("address");
                    String mapID_get = (String) document.get("maps-id");
                    Log.d("Firebase_mapID2",mapID_get);
                    if (mapID_get != null) {
                        Log.d("Firestore", "User ID: " + document.getId() +
                                " Username: " + mapID);
                        if(mapID_get.equals(mapID))
                        {
                            tvCarAddress.setText(address_get);
                            Log.d("Firebase","Address");
                            break;
                        }
                    }
                }
            }
        });
    }
    private void showLogoutConfirmationDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AmbulancePersonal.this);
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, id) -> {
                    Intent intent = new Intent(AmbulancePersonal.this,SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    saveCredentials(username);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void saveCredentials(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.putString("email", email);
        editor.putString("password","123456");
        editor.apply(); // Save new data

    }
}
