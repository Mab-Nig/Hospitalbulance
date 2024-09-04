package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import project.cs426.hospitalbulance.backend.Authenticator;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);

		Button patientButton = findViewById(R.id.patientButton);
		Button ambulanceButton = findViewById(R.id.ambulanceButton);
		Button hospitalButton = findViewById(R.id.hospitalButton);
		Button sendOtpButton = findViewById(R.id.sendOtpButton);
        EditText phoneEditText = findViewById(R.id.phoneEditText);
        EditText otpEditText = findViewById(R.id.otpEditText);

        Authenticator auth = new Authenticator().setContext(this);

        sendOtpButton.setOnClickListener(v -> {
            auth.setPhoneNumber(phoneEditText.getText().toString())
                    .sendSmsOtp();
        });

        loginButton.setOnClickListener(v -> {
            auth.setOnSuccessListener(() -> {
						Intent intent = new Intent(LoginActivity.this, HomeScreenHomeActivity.class);
						startActivity(intent);
					})
                    .signInWithCode(otpEditText.getText().toString());
        });

        // Handle the back arrow click
        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Handle the button selections

    }
}
