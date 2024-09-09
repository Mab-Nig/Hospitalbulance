package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button patientButton = findViewById(R.id.patientButton);
        Button hospitalButton = findViewById(R.id.hospitalButton);
        Button ambulanceButton = findViewById(R.id.ambulanceButton);

        // Patient button
        patientButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreen.this, SignupActivity.class);
            startActivity(intent);
        });

        // Ambulance button
        ambulanceButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreen.this, AmbulanceRegistrationActivity.class);
            startActivity(intent);
        });

        // Hospital button
        hospitalButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreen.this, HospitalRegistrationActivity.class);
            startActivity(intent);
        });
    }
}