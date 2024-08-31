package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button patientButton, ambulanceButton, hospitalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        ImageButton backArrowButton = findViewById(R.id.backArrowButton);

        patientButton = findViewById(R.id.patientButton);
        ambulanceButton = findViewById(R.id.ambulanceButton);
        hospitalButton = findViewById(R.id.hospitalButton);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeScreenHomeActivity.class);
            startActivity(intent);
        });

        // Handle the back arrow click
        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Handle the button selections

    }
}
