package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
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

import project.cs426.hospitalbulance.backend.Authenticator;

public class AmbulanceRegistrationActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
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
}
