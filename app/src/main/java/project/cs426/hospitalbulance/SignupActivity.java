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

public class SignupActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);

        registerButton.setOnClickListener(v -> createNewAccount("patient"));

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        backArrowButton.setOnClickListener(v -> onBackPressed());
    }

    private void createNewAccount(String userType) {
        String email = this.emailEditText.getText().toString().trim();
        String password = this.passwordEditText.getText().toString().trim();

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
                        if (userType.equals("patient")) {
                            Intent intent = new Intent(SignupActivity.this, HomeScreenHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure() {}
                })
                .signUp("patient");
    }
}
