package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        ImageButton fbLoginButton = findViewById(R.id.fbLoginButton);
        ImageButton gmailLoginButton = findViewById(R.id.gmailLoginButton);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        autoFillLogin(emailEditText, passwordEditText);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeScreenHomeActivity.class);
            startActivity(intent);
        });

        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Handle Facebook login
        fbLoginButton.setOnClickListener(v -> {

        });

        // Handle Gmail login
        gmailLoginButton.setOnClickListener(v -> {

        });

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