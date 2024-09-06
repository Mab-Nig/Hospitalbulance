package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import project.cs426.hospitalbulance.backend.Authenticator;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        ImageButton fbLoginButton = findViewById(R.id.fbLoginButton);  // New button
        ImageButton gmailLoginButton = findViewById(R.id.gmailLoginButton);  // New button
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        Authenticator authenticator = new Authenticator().setContext(this);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                final String msg = "Cannot leave empty fields.";
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            authenticator.setEmail(email)
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
        });

        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Handle Facebook login
        fbLoginButton.setOnClickListener(v -> {

        });

        // Handle Gmail login
        gmailLoginButton.setOnClickListener(v -> {

        });
    }
}
