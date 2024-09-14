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

import project.cs426.hospitalbulance.backend.Authenticator;
import project.cs426.hospitalbulance.backend.database.Collections;

public class HospitalRegistrationActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        TextView loginTextView = findViewById(R.id.loginTextView);

        backArrowButton.setOnClickListener(v -> onBackPressed());

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(HospitalRegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> createNewAccount("hospital"));
    }

    private void createNewAccount(String userType) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        this.authenticator.setEmail(email)
                .setPassword(password)
                .setOnCompleteListener(new Authenticator.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        if (userType.equals("hospital")) {
                            updateHospitalInfo(email);
                            Intent intent = new Intent(HospitalRegistrationActivity.this,
                                    HospitalScreen.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure() {}
                })
                .signUp("hospital");
    }

    private void updateHospitalInfo(String email) {
        this.db.collection(Collections.HOSPITALS)
                .whereEqualTo("email", email)
                .limit(1L)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Log.e("HospitalRegistrationActivity",
                                "updateHospitalInfo:Hospital" + email + " not found");
                        return;
                    }

                    DocumentSnapshot snapshot = querySnapshot.getDocuments().get(0);
                    String name = ((EditText)findViewById(R.id.hospitalnameEditText)).getText().toString();
                    String address = ((EditText)findViewById(R.id.hospitaladdressEditText)).getText().toString();
                    snapshot.getReference()
                            .update("name", name,
                                    "address", address)
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.e("HospitalRegistrationActivity",
                                            "updateHospitalInfo:Hospital" + email + " update failure.");
                                    return;
                                }

                                Log.d("HospitalRegistrationActivity",
                                        "updateHospitalInfo:Hospital" + email + " update success.");

                            });
                });
    }
}
