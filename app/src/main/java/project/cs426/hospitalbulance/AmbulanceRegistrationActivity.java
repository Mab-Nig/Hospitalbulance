package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.StructuredQuery;

import project.cs426.hospitalbulance.backend.Authenticator;
import project.cs426.hospitalbulance.backend.database.Collections;

public class AmbulanceRegistrationActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        backArrowButton.setOnClickListener(v -> onBackPressed());

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(AmbulanceRegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> createNewAccount("ambulance"));
    }

    private void createNewAccount(String userType) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        this.authenticator.setEmail(email)
                .setPassword(password)
                .setOnCompleteListener(new Authenticator.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        if (userType.equals("ambulance")) {
                            updateAmbulanceInfo(email);
                            Intent intent = new Intent(AmbulanceRegistrationActivity.this,
                                    HomeScreenHomeDriverActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure() {}
                })
                .signUp("ambulance");
    }

    private void updateAmbulanceInfo(String email) {
        this.db.collection(Collections.AMBULANCES)
                .whereEqualTo("email", email)
                .limit(1L)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.e("AmbulanceRegistrationActivity",
                                "updateAmbulanceInfo:Car" + email + " not found");
                        return;
                    }

                    DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                    String ownerEmail = ((EditText)findViewById(R.id.ambulanceEmailEditText)).getText().toString();
                    String id = ((EditText)findViewById(R.id.ambulanceIdEditText)).getText().toString();
                    String model = ((EditText)findViewById(R.id.ambulanceModelEditText)).getText().toString();
                    snapshot.getReference()
                            .update("owner_email", ownerEmail,
                                    "car_id", id,
                                    "car_model", model)
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("AmbulanceRegistrationActivity",
                                            "updateAmbulanceInfo:Car" + email + " update failure.");
                                } else {
                                    Log.e("AmbulanceRegistrationActivity",
                                            "updateAmbulanceInfo:Car" + email + " update success.");
                                }
                            });
                });
    }
}
