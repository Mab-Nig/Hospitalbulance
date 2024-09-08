package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyDetail extends AppCompatActivity {

    private TextView patientName, patientAge, patientSex, accidentType, ambulanceService, currentStatus;
    private Button acceptButton;
    private Notification notification;
    private boolean isFromRecord;  // To check if the notification is from the record screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_detail);

        // Initialize UI elements
        patientName = findViewById(R.id.patientName);
        patientAge = findViewById(R.id.patientAge);
        patientSex = findViewById(R.id.patientSex);
        accidentType = findViewById(R.id.accidentType);
        ambulanceService = findViewById(R.id.ambulanceService);
        currentStatus = findViewById(R.id.currentStatus);
        acceptButton = findViewById(R.id.acceptButton);

        // Retrieve the Notification object passed from the previous activity
        notification = (Notification) getIntent().getSerializableExtra("notification");
        isFromRecord = getIntent().getBooleanExtra("isFromRecord", false);

        ImageButton backArrowButton = findViewById(R.id.backArrowButton);
        backArrowButton.setOnClickListener(v -> onBackPressed());

        // Populate the fields with data from the Notification object and other static data
        if (notification != null) {
            accidentType.setText(notification.getTypeOfEmergency());  // Populating accident type from notification
        } else {
            accidentType.setText("Unknown Emergency");
        }

        // Static data for now (you can modify these with actual values later)
        patientName.setText("John Doe");
        patientAge.setText("35");
        patientSex.setText("Male");
        ambulanceService.setText("ABC Ambulance Service");
        currentStatus.setText("Critical");

        // If the notification is from Record Screen, hide the Accept button
        if (isFromRecord) {
            acceptButton.setVisibility(Button.GONE);
        } else {
            acceptButton.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("notification", notification);
                setResult(RESULT_OK, resultIntent);
                finish();  // Close the EmergencyDetail screen and return the result to the Home screen
            });
        }
    }
}