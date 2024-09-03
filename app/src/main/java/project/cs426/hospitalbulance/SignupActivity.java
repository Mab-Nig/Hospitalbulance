package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.checkerframework.checker.units.qual.A;

import project.cs426.hospitalbulance.backend.Authenticator;

public class SignupActivity extends AppCompatActivity {
    private EditText nameEditText, phoneEditText, otpEditText;
    private Button sendOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button registerButton = findViewById(R.id.registerButton);
        TextView loginTextView = findViewById(R.id.loginTextView);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        otpEditText = findViewById(R.id.otpEditText);

        Authenticator auth = new Authenticator().setContext(this);

        sendOtpButton.setOnClickListener(v -> {
            auth.setPhoneNumber(phoneEditText.getText().toString())
                    .sendSmsOtp();
        });

        registerButton.setOnClickListener(v -> {
            auth.setOnSuccessListener(() -> {
						Intent intent = new Intent(SignupActivity.this, HomeScreenHomeActivity.class);
						startActivity(intent);
					})
                    .signInWithCode(otpEditText.getText().toString());
        });

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        backArrowButton.setOnClickListener(v -> onBackPressed());
    }
}