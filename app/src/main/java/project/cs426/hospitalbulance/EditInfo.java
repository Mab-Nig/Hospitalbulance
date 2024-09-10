package project.cs426.hospitalbulance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class EditInfo extends AppCompatActivity {
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);
        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        EditText email = findViewById(R.id.email);
        EditText bloodType = findViewById(R.id.blood_type);
        EditText address = findViewById(R.id.address);
        Button savebtn = findViewById(R.id.save_edit_info);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(getApplicationContext());
        }

// Get Firestore instance
        db = FirebaseFirestore.getInstance();
        readDataGeneral("user@gmail.com",firstName,lastName,email,bloodType,address);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDataGeneral("username@gmail.com",firstName, lastName, email, bloodType, address);
            }
        });
    }
    private void readDataGeneral(String username,EditText firstName, EditText lastName, EditText email, EditText bloodType, EditText address) {
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", ":InsideReadData");
                for (QueryDocumentSnapshot document : task.getResult()) {

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

                            }
                            if (generalInfo != null) {
                                String bloodType_str = (String) generalInfo.get("blood-type");
                                bloodType.setText(bloodType_str + " +ve");
                                Log.d("Blood", "Success" + bloodType_str);

                                String firstName_str = (String) generalInfo.get("first-name");
                                firstName.setText(firstName_str);
                                Log.d("FirstName", "Success" + firstName_str);

                                String lastName_str = (String) generalInfo.get("last-name");
                                lastName.setText(lastName_str);
                                Log.d("LastName", "Success" + lastName_str);


                                email.setText(username);


                                String address_str = (String) generalInfo.get("address");
                                address.setText(address_str);
                                Log.d("Address", "Success" + address_str);

                            }


                        }
                    }

                }
            } else {
                Log.e("Firestore", "Error getting documents: ", task.getException());
            }
        });
        }
    private void writeDataGeneral(String username,EditText firstName, EditText lastName, EditText email, EditText bloodType, EditText address) {
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firestore", ":InsideReadData");
                for (QueryDocumentSnapshot document : task.getResult()) {

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

                            }
                            if (generalInfo != null) {
                                String bloodType_str = (String) generalInfo.get("blood-type");
                                bloodType.setText(bloodType_str + " +ve");


                                String firstName_str = (String) generalInfo.get("first-name");
                                firstName.setText(firstName_str);


                                String lastName_str = (String) generalInfo.get("last-name");
                                lastName.setText(lastName_str);



                                email.setText(username);


                                String address_str = (String) generalInfo.get("address");
                                address.setText(address_str);
                                db.collection("users").document(username).update("general-info.blood-type", bloodType.getText().toString());
                                db.collection("users").document(username).update("general-info.first-name", firstName.getText().toString());
                                db.collection("users").document(username).update("general-info.last-name", lastName.getText().toString());
                                db.collection("users").document(username).update("general-info.address", address.getText().toString());
                                db.collection("users").document(username).update("login-info.username", email.getText().toString());
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

}
