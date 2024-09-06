package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import project.cs426.hospitalbulance.backend.Authenticator;

public class SignupActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private EditText emailEditText, passwordEditText;
    private ImageButton patientButton, ambulanceButton, hospitalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.emailEditText = findViewById(R.id.emailEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);

        this.patientButton = findViewById(R.id.patientButton);
        this.ambulanceButton = findViewById(R.id.ambulanceButton);
        this.hospitalButton = findViewById(R.id.hospitalButton);
        setRoleSelectListener();

        registerButton.setOnClickListener(v -> createNewAccount());

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        backArrowButton.setOnClickListener(v -> onBackPressed());
    }

    private void setRoleSelectListener() {
        this.patientButton.setActivated(true);
        this.ambulanceButton.setActivated(false);
        this.hospitalButton.setActivated(false);

        this.patientButton.setOnClickListener(v -> {
            this.patientButton.setActivated(true);
            this.ambulanceButton.setActivated(false);
            this.hospitalButton.setActivated(false);
        });
        this.ambulanceButton.setOnClickListener(v -> {
            this.patientButton.setActivated(false);
            this.ambulanceButton.setActivated(true);
            this.hospitalButton.setActivated(false);
        });
        this.hospitalButton.setOnClickListener(v -> {
            this.patientButton.setActivated(false);
            this.ambulanceButton.setActivated(false);
            this.hospitalButton.setActivated(true);
        });
    }

    private String getSelectedRole() {
        if (this.patientButton.isActivated()) {
            return "patient";
        }
        if (this.ambulanceButton.isActivated()) {
            return "ambulance_owner";
        }
        return "hospital";
    }
    
    private void createNewAccount() {
        String email = this.emailEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            final String msg = "Cannot leave empty fields.";
            Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        this.authenticator.setEmail(email)
                .setPassword(password)
                .setOnCompleteListener(new Authenticator.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignupActivity.this, HomeScreenHomeActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure() {}
                })
                .signUp(getSelectedRole());
    }
}