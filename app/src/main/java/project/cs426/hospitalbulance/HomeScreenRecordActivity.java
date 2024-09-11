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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.OtherMedicalInfo;
import project.cs426.hospitalbulance.backend.database.Patient;

public class HomeScreenRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeScreenRecordActivity";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_record);

        readPatientDocument();

        Button save = findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#808080"));

        EditText edit = findViewById(R.id.add_body);
        edit.setOnFocusChangeListener((v, hasFocus) -> {
            String content = String.valueOf(edit.getText());
            if (hasFocus) {
                if (!content.isEmpty()) {
                    save.setBackgroundColor(Color.parseColor("#C53434"));
                }
                // EditText gained focus
            } else {
                // EditText lost focus
                if (!content.isEmpty()) {
                    save.setBackgroundColor(Color.parseColor("#C53434"));
                }
            }
        });
    }

    private void prepareContent(Patient patient)
            throws InvocationTargetException, IllegalAccessException {
        RecyclerView bodyDetail = findViewById(R.id.body_measure);
        RecyclerView medicationDetail = findViewById(R.id.medications);
        RecyclerView symptomDetail = findViewById(R.id.symptoms);
        RecyclerView otherDataDetail = findViewById(R.id.other_data);

        bodyDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        medicationDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        symptomDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        otherDataDetail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

    private void readPatientDocument() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser()
                .getUid();
        this.db.collection(Collections.PATIENTS).document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    final String msg = "readPatientDocument:Document ID: "
                            + documentSnapshot.getId();

                    if (!documentSnapshot.exists()) {
                        Log.e(TAG, msg + "does not exist.");
                        return;
                    }

                    Log.d(TAG, msg);
                    Log.d(TAG, "readPatientDocument:documentSnapshot: "
                            + documentSnapshot.getData());
                    try {
                        prepareContent(documentSnapshot.toObject(Patient.class));
                    } catch (Exception ignored) {}
                });
    }

    private List<String> readBodyMeasurements(@NonNull Patient patient) {
        List<String> results = new ArrayList<>();
        results.add("Weight - "
                + Double.valueOf(patient.getMedicalInfo().getBodyMeasurements()
                        .getWeight())
                .toString());
        results.add("Height - "
                + Double.valueOf(patient.getMedicalInfo().getBodyMeasurements()
                        .getHeight())
                .toString());
        return results;
    }

    private List<String> readMedications(@NonNull Patient patient) {
        return patient.getMedicalInfo().getMedications();
    }

    private List<String> readSymptoms(@NonNull Patient patient) {
        return patient.getMedicalInfo().getSymptoms();
    }

    private List<String> readOtherData(@NonNull Patient patient)
            throws InvocationTargetException, IllegalAccessException {
        final Map<String, String> fieldToDisplay = new HashMap<>();
        fieldToDisplay.put("alcoholAssumption", "Alcohol Assumption");
        fieldToDisplay.put("isInhalerUsed", "Inhaler Usage");
        fieldToDisplay.put("isDiabetic", "Diabetic");
        fieldToDisplay.put("fallenCnt", "Number of Times Fallen");
        fieldToDisplay.put("allergies", "Allergies");
        fieldToDisplay.put("specialAbilities", "Special Abilities");

        final OtherMedicalInfo other = patient.getMedicalInfo().getOther();
        List<String> results = new ArrayList<>();
        for (Method method : other.getClass().getDeclaredMethods()) {
            final String methodName = method.getName();
            final boolean methodStartsWithGet = StringUtils.startsWith(methodName, "get");
            final boolean methodStartsWithIs = StringUtils.startsWith(methodName, "is");

            if (!methodStartsWithGet && !methodStartsWithIs) {
                continue;
            }

            String fieldName;
            if (methodStartsWithGet) {
                fieldName = StringUtils.removeStart(methodName, "get");
                fieldName = StringUtils.uncapitalize(fieldName);
            } else {
                fieldName = StringUtils.uncapitalize(methodName);
            }
            Log.d(TAG, "readOtherData:fieldName: " + fieldName);
            results.add(fieldToDisplay.get(fieldName) + " - "
                    + otherFieldValueToString(method.invoke(other)));
        }
        return results;
    }

    private String otherFieldValueToString(@NonNull Object value) {
        if (value instanceof Boolean) {
            if ((boolean)value) {
                return "Yes";
            }
            return "No";
        }

        if (value instanceof Integer) {
            return value.toString();
        }

        if (value instanceof String) {
            return StringUtils.capitalize(value.toString());
        }

        if (value instanceof List) {
            List<Object> valueList = (List<Object>)value;
            List<String> valueString = new ArrayList<>(valueList.size());
            for (int i = 0; i < valueString.size(); ++i) {
                valueString.set(i, valueList.get(i).toString());
            }
            return String.join(", ", valueString);
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.save_button)) {
            //Input update data (change and new data) to the database and reload the screen
        } else if (v == findViewById(R.id.home_button)) {
            Intent intent = new Intent(this, HomeScreenHomeActivity.class);
            this.startActivity(intent);
        }
    }
}
