package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import project.cs426.hospitalbulance.backend.Authenticator;

public class LoginActivity extends AppCompatActivity {
    private final Authenticator authenticator = new Authenticator().setContext(this);
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailEditText = findViewById(R.id.emailEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
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
        String email = this.emailEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            final String msg = "Cannot leave empty fields.";
            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        this.authenticator.setEmail(email)
                .setPassword(password)
                .setOnCompleteListener(new Authenticator.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(LoginActivity.this,
                                HomeScreenHomeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure() {}
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
}
