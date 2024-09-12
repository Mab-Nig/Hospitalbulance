package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import project.cs426.hospitalbulance.backend.Authenticator;
import project.cs426.hospitalbulance.backend.database.Collections;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        ImageButton fbLoginButton = findViewById(R.id.fbLoginButton);
        ImageButton gmailLoginButton = findViewById(R.id.gmailLoginButton);
        
        autoFillLogin(emailEditText, passwordEditText);

        loginButton.setOnClickListener(v -> loginUser());

        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Handle Facebook login
        fbLoginButton.setOnClickListener(v -> {
            // TODO: Implement Facebook login
        });

        // Handle Gmail login
        gmailLoginButton.setOnClickListener(v -> {
            // TODO: Implement Gmail login
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email.");
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            passwordEditText.requestFocus();
            return;
        }

        this.authenticator.setEmail(email)
                .setPassword(password)
                .setOnCompleteListener(new Authenticator.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        checkUserRole(email);
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(LoginActivity.this, "Incorrect email or password.", Toast.LENGTH_LONG).show();
                    }
                })
                .signIn();
    }
    
    private void autoFillLogin(EditText emailEditText, EditText passwordEditText) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Retrieve saved email and password
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        // Check if both are not null (meaning user credentials are saved)
        if (savedEmail != null && savedPassword != null) {
            // Auto-fill email and password into EditText fields
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
        }
    }

    private void checkUserRole(String email) {
        this.db.collection(Collections.USERS)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                        "Error checking role: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String role = document.getString("role");

                        if (role != null) {
                            switch (role) {
                                case "patient":
                                    // Navigate to HomeScreenHomeActivity
                                    Intent userIntent = new Intent(LoginActivity.this, HomeScreenHomeActivity.class);
                                    userIntent.putExtra("username", email);
                                    startActivity(userIntent);
                                    finish();
                                    break;

                                case "hospital":
                                    // Navigate to HospitalHomeScreen
                                    Intent hospitalIntent = new Intent(LoginActivity.this, HospitalHomeScreen.class);
                                    hospitalIntent.putExtra("username", email);
                                    startActivity(hospitalIntent);
                                    finish();
                                    break;

                                case "ambulance":
                                    // Handle ambulance case later
                                    Intent ambulanceIntent = new Intent(LoginActivity.this, HomeScreenHomeDriverActivity.class);
                                    ambulanceIntent.putExtra("username", email);
                                    startActivity(ambulanceIntent);
                                    finish();
                                    break;

                                default:
                                    Toast.makeText(LoginActivity.this, "Unknown role.", Toast.LENGTH_LONG)
                                            .show();
                                    break;
                            }
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Client data not found.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
