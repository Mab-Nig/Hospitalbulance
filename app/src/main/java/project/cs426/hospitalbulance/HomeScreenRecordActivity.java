package project.cs426.hospitalbulance;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.Patient;

public class HomeScreenRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeScreenRecordActivity";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_record);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        prepareContent(username);
        Button save = findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#808080"));

        EditText edit = findViewById(R.id.add_body);
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String content = String.valueOf(edit.getText());
                    if (!content.isEmpty())
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#C53434"));
                    }
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(edit.getText());
                    if (!content.isEmpty())
                    {
                        Button save = findViewById(R.id.save_button);
                        save.setBackgroundColor(Color.parseColor("#C53434"));
                    }
                }
            }
        });
    }

    private void prepareContent(String username) {
		RecyclerView bodyDetail = findViewById(R.id.body_measure);
		RecyclerView medicationDetail = findViewById(R.id.medications);
		RecyclerView symptomDetail = findViewById(R.id.symptoms);
		RecyclerView otherDataDetail = findViewById(R.id.other_data);

        bodyDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        medicationDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        symptomDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherDataDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Patient patient = readPatientDocument();

        List<String> bodyMeasurements = readBodyMeasurements(patient);
        List<String> medications = readMedications(patient);
        List<String> symptoms = readSymptoms(patient);
        List<String> otherData = readOtherData(patient);

        DetailRecordAdapter bodyMeasurementsAdapter = new DetailRecordAdapter(bodyMeasurements);
        DetailRecordAdapter medicationsAdapter = new DetailRecordAdapter(medications);
        DetailRecordAdapter symptomsAdapter = new DetailRecordAdapter(symptoms);
        DetailRecordAdapter otherDataAdapter = new DetailRecordAdapter(otherData);

        bodyDetail.setAdapter(bodyMeasurementsAdapter);
        medicationDetail.setAdapter(medicationsAdapter);
        symptomDetail.setAdapter(symptomsAdapter);
        otherDataDetail.setAdapter(otherDataAdapter);
    }

    private Patient readPatientDocument() {
        AtomicReference<Patient> patient = new AtomicReference<>();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.db.collection(Collections.PATIENTS).document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    final String msg = "readOtherData:Document ID: " + documentSnapshot.getId();

                    if (!documentSnapshot.exists()) {
                        Log.e(TAG, msg + "does not exist.");
                        return;
                    }

                    Log.d(TAG, msg);
                    patient.set(documentSnapshot.toObject(Patient.class));
                });
        return patient.get();
    }

    private List<String> readBodyMeasurements(@NonNull Patient patient) {
        List<String> results = new ArrayList<>();
        Map<String, Object> bodyMeasurements = patient.getMedicalInfo()
                .getBodyMeasurements();
        for (Map.Entry<String, Object> entry : bodyMeasurements.entrySet()) {
            String[] keyNames = entry.getKey().split("_");
            for (int i = 0; i < keyNames.length; ++i) {
                keyNames[i] = StringUtils.capitalize(keyNames[i]);
            }
            results.add(String.join(" ", keyNames) + " - " + entry.getValue().toString());
        }
        return results;
    }

    private List<String> readMedications(@NonNull Patient patient) {
        return patient.getMedicalInfo().getMedications();
    }

    private List<String> readSymptoms(@NonNull Patient patient) {
        return patient.getMedicalInfo().getSymptoms();
    }

    private List<String> readOtherData(@NonNull Patient patient) {
        List<String> results = new ArrayList<>();
        Map<String, Object> other = patient.getMedicalInfo().getOther();
        for (Map.Entry<String, Object> entry : other.entrySet()) {
            String fieldName;
            if (entry.getKey().equals("fallen_cnt")) {
                fieldName = "Number of Times Fallen";
            } else {
                String[] keyNames = entry.getKey().split("_");
                for (int i = 0; i < keyNames.length; ++i) {
                    keyNames[i] = StringUtils.capitalize(keyNames[i]);
                }
                fieldName = String.join(" ", keyNames);
            }
            results.add(fieldName + " - " + entry.getValue().toString());
        }
        return results;
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.save_button))
        {
            //Input update data (change and new data) to the database and reload the screen
        }
        else if(v == findViewById(R.id.home_button))
        {
            Intent intent = new Intent(this, HomeScreenHomeActivity.class);
            this.startActivity(intent);
        }
    }
}
