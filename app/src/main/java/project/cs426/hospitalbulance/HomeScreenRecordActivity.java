package project.cs426.hospitalbulance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.Patient;

public class HomeScreenRecordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db;
    private  RecyclerView bodyDetail ;
    private RecyclerView medicationDetail ;
    private RecyclerView symptonDetail ;
    private RecyclerView otherDataDetail ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_record);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        // Get Firestore instance
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        prepareContent(username);
        Button save = findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#808080"));



        EditText edit = findViewById(R.id.add_body);
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String content = String.valueOf(edit.getText());
                    if (!content.isEmpty())
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#C53434"));
                    }
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(edit.getText());
                    if (!content.isEmpty())
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#C53434"));
                    }
                }
            }
        });

    }

    private void prepareContent(String username) {
        bodyDetail = findViewById(R.id.body_measure);
        medicationDetail = findViewById(R.id.medications);
        symptonDetail = findViewById(R.id.symptons);
        otherDataDetail = findViewById(R.id.other_data);

        bodyDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        medicationDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        symptonDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherDataDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<String> details = new ArrayList<>();
        List<String> details1 = new ArrayList<>();
        List<String> details2 = new ArrayList<>();
        List<String> details3 = new ArrayList<>();

        //read body measurement
        readBodyMeasure(username, details);

        // DetailRecordAdapter bodyMeasurementsAdapter = new DetailRecordAdapter(bodyMeasurements);
        // DetailRecordAdapter medicationsAdapter = new DetailRecordAdapter(medications);
        // DetailRecordAdapter symptomsAdapter = new DetailRecordAdapter(symptoms);
        // DetailRecordAdapter otherDataAdapter = new DetailRecordAdapter(otherData);

        //read medications
        readMedications(username, details1);


        //read sympto
        readSympton(username, details2);

        //read other data
        readotherData(username, details3);

    }

    private void readotherData(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }

        CollectionReference usersRef = db.collection("users");
        // do querry to get data
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the 'general-info' map
                    Map<String, Object> medicalInfo = (Map<String, Object>) document.get("medical-info");
                    // Access the 'login-info' map
                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");

                    if (loginInfo != null) {
                        // Example: Access fields in 'login-info' map
                        String username_get = (String) loginInfo.get("username");
                        // Check and use the data
                        if (username_get != null ) {
                            Log.d("Firestore", "User ID: " + document.getId() +
                                    " Username: " + username_get );
                            if(username_get.equals(username))
                            {
                                if (medicalInfo != null) {
                                    ArrayList<String> listOtherdata = (ArrayList<String>) medicalInfo.get("Other data");
                                    ArrayList<String> listAllergies = (ArrayList<String>) medicalInfo.get("allergies");
                                    Number fallen = (Number) medicalInfo.get("fallen-cnt");
                                    boolean diabetic = (boolean) medicalInfo.get("is-diabetic");
                                    // Check for null values and handle appropriately
                                    if (listOtherdata != null) {
                                        Log.d("Firestore", "User ID: " + document.getId());
                                        for(int i  = 0; i < listOtherdata.size(); i++)
                                        {
                                            details.add(listOtherdata.get(i));
                                        }
                                        if(fallen != null) {
                                            details.add("Number of times fallen - " + fallen);
                                        }
                                        else {
                                            details.add("Number of times fallen - None");
                                        }
                                        if(diabetic == true )
                                        {
                                            details.add("Diabetic - Yes");
                                        }
                                        else {
                                            details.add("Diabetic - None");
                                        }

                                        if(listAllergies.isEmpty())
                                        {
                                            details.add("Allergies - None");
                                        }
                                        else {
                                            for(int i =0; i < listAllergies.size(); i++)
                                            {
                                                details.add("Allergies - " + listAllergies.get(i));
                                            }
                                        }
                                        DetailRecordAdapter adapter3 = new DetailRecordAdapter(details);
                                        otherDataDetail.setAdapter(adapter3);
                                        break;
                                    } else {
                                        Log.d("Firestore", "Missing medication in medical-info for User ID: " + document.getId());
                                    }
                                } else {
                                    Log.d("Firestore", "Missing medical-info for User ID: " + document.getId());
                                }
                            }
                        } else {
                            Log.d("Firestore", "Missing username or email in login-info for User ID: " + document.getId());
                        }
                    } else {
                        Log.d("Firestore", "Missing login-info for User ID: " + document.getId());
                    }
                }
            } else {
                Log.w("Firestore", "Error getting documents.", task.getException());
            }
        });

//        details.add("Alcohol Consumption - Light");
//        details.add("Inhaler Usage - No");
//        details.add("Number of times fallen - 2");
//
//
//        details.add("Diabetic - No");
//        details.add("Allergies - None");
//        details.add("Special Abilities - None");

    }

    private void readSympton(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        CollectionReference usersRef = db.collection("users");
        // do querry to get data
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the 'medical-info' map
                    Map<String, Object> medicalInfo = (Map<String, Object>) document.get("medical-info");
                    // Access the 'login-info' map
                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");

                    if (loginInfo != null) {
                        // Example: Access fields in 'login-info' map
                        String username_get = (String) loginInfo.get("username");
                        // Check and use the data
                        if (username_get != null ) {
                            Log.d("Firestore", "User ID: " + document.getId() +
                                    " Username: " + username_get );
                            if(username_get.equals(username))
                            {
                                if (medicalInfo != null) {
                                    // Access 'weight' and 'height' within 'general-info' map
                                    ArrayList<String> listSymptom = (ArrayList<String>) medicalInfo.get("Symptom");
                                    // Check for null values and handle appropriately
                                    if (listSymptom != null) {
                                        Log.d("Firestore", "User ID: " + document.getId());
                                        for(int i  = 0; i < listSymptom.size(); i++)
                                        {
                                            details.add(listSymptom.get(i));
                                        }
                                        DetailRecordAdapter adapter2 = new DetailRecordAdapter(details);
                                        symptonDetail.setAdapter(adapter2);
                                        break;
                                    } else {
                                        Log.d("Firestore", "Missing medication in medical-info for User ID: " + document.getId());
                                    }
                                } else {
                                    Log.d("Firestore", "Missing medical-info for User ID: " + document.getId());
                                }
                            }
                        } else {
                            Log.d("Firestore", "Missing username or email in login-info for User ID: " + document.getId());
                        }
                    } else {
                        Log.d("Firestore", "Missing login-info for User ID: " + document.getId());
                    }
                }
            } else {
                Log.w("Firestore", "Error getting documents.", task.getException());
            }
        });


//        details.add("Pain");
//        details.add("Abdominal Cramps");
//        details.add("Bloating");
    }

    private void readMedications(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }

        this.db.collection(Collections.PATIENTS).whereEqualTo("email", username)
                .get().addOnSuccessListener(querySnapshot -> {
                   for( DocumentSnapshot result : querySnapshot.getDocuments())
                   {
                       Patient man = result.toObject(Patient.class);
                       Log.d("READ DATA FIREBASE", "readMedications: " + result.getData());
                   }
                });

//        details.add("Combiflam Tablet");
//        details.add("Duloxetine");
//        details.add("Lubricating Injections");
    }

    private void readBodyMeasure(String username, List<String> details) {
        if(!details.isEmpty())
        {
            details.clear();
        }
        // Reference to the users collection
        CollectionReference usersRef = db.collection("users");
       // do querry to get data
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the 'general-info' map
                    Map<String, Object> generalInfo = (Map<String, Object>) document.get("general-info");
                    // Access the 'login-info' map
                    Map<String, Object> loginInfo = (Map<String, Object>) document.get("login-info");

                    if (loginInfo != null) {
                        // Example: Access fields in 'login-info' map
                        String username_get = (String) loginInfo.get("username");
                        // Check and use the data
                        if (username_get != null ) {
                            Log.d("Firestore", "User ID: " + document.getId() +
                                    " Username: " + username_get );
                            if(username_get.equals(username))
                            {
                                if (generalInfo != null) {
                                    // Access 'weight' and 'height' within 'general-info' map
                                    Number weight = (Number) generalInfo.get("weight"); // Use appropriate casting based on your data type
                                    Number height = (Number) generalInfo.get("height"); // Use appropriate casting based on your data type

                                    // Check for null values and handle appropriately
                                    if (weight != null && height != null) {
                                        Log.d("Firestore", "User ID: " + document.getId() +
                                                " Weight: " + weight + " Height: " + height);
                                        details.add("Weight - " + weight +"kg" );
                                        details.add("Height - " + height +"cm");
                                        DetailRecordAdapter adapter = new DetailRecordAdapter(details);
                                        bodyDetail.setAdapter(adapter);
                                        break;
                                    } else {
                                        Log.d("Firestore", "Missing weight or height in general-info for User ID: " + document.getId());
                                    }
                                } else {
                                    Log.d("Firestore", "Missing general-info for User ID: " + document.getId());
                                }
                            }
                        } else {
                            Log.d("Firestore", "Missing username or email in login-info for User ID: " + document.getId());
                        }
                    } else {
                        Log.d("Firestore", "Missing login-info for User ID: " + document.getId());
                    }
                }
            } else {
                Log.w("Firestore", "Error getting documents.", task.getException());
            }
        });

    }


    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.save_button))
        {
            //Input update data (change and new data) to the database and reload the screen
        }
        else if(v == findViewById(R.id.home_button))
        {
            Intent intent = new Intent(this, HomeScreenHomeActivity.class);
            this.startActivity(intent);
        }
        else if(v == findViewById(R.id.personal_button))
        {
            Intent intent = new Intent(this, HomeScreenPersonalActivity.class);
            //intent.putExtra("username", username);
            this.startActivity(intent);
        }
    }
}
