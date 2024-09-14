package project.cs426.hospitalbulance;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import project.cs426.hospitalbulance.backend.database.Ambulance;
import project.cs426.hospitalbulance.backend.database.Collections;

public class DriverPersonalFragment extends Fragment {

    private Switch switchShowAddress;
    private ScrollView scrollViewAddress;
    private FirebaseFirestore db;
    private EditText etCarIdPer;
    private EditText etCarModelPer;
    private TextView tvCarAddress;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ambulance_personal, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchShowAddress = view.findViewById(R.id.switch_show_address);
        scrollViewAddress = view.findViewById(R.id.scroll_view_address);
        etCarIdPer = view.findViewById(R.id.et_car_id_per);
        etCarModelPer = view.findViewById(R.id.et_car_model_per);
        tvCarAddress = view.findViewById(R.id.tv_car_address);
        // Initialize Firebase
        // Initialize Firebase if it hasn't been initialized yet
        if (FirebaseApp.getApps(view.getContext()).isEmpty()) {
            FirebaseApp.initializeApp(view.getContext());
        }

        // Get Firestore instance
        db = FirebaseFirestore.getInstance();
        // Set listener for the switch
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String username = currentUser.getEmail();
        readData(username,etCarIdPer,etCarModelPer,switchShowAddress,scrollViewAddress,tvCarAddress);
        view.findViewById(R.id.btn_logout_am).setOnClickListener(v -> showLogoutConfirmationDialog(username));
        view.findViewById(R.id.btn_save_am).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData(username,etCarIdPer,etCarModelPer,switchShowAddress,scrollViewAddress,tvCarAddress);
            }
        });
        switchShowAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show the address when switch is ON
                scrollViewAddress.setVisibility(ScrollView.VISIBLE);
            } else {
                // Hide the address when switch is OFF
                scrollViewAddress.setVisibility(ScrollView.GONE);
            }
        });

    }

    private void readData(String username, EditText etCarIdPer, EditText etCarModelPer,Switch switchShowAddress,ScrollView scrollViewAddress,TextView tvCarAddress) {
        this.db.collection(Collections.AMBULANCES)
                .whereEqualTo("email",username)
                .get()
                .addOnSuccessListener(querySnapshot-> {
                    for (DocumentSnapshot result : querySnapshot.getDocuments()) {
                        Log.d("Firestore", username);
                        Ambulance ambulance = result.toObject(Ambulance.class);

                        if(ambulance != null)
                        {
                            etCarIdPer.setText(ambulance.getCarID());
                            etCarModelPer.setText(ambulance.getCarModel());
                            switchShowAddress.setChecked(ambulance.isAvailable());
                            if(ambulance.isAvailable())
                            {


                                scrollViewAddress.setVisibility(ScrollView.VISIBLE);

                            } else {
                                scrollViewAddress.setVisibility(ScrollView.GONE);
                            }
                        }
                    }
                });
    }
    private void saveData(String username, EditText etCarIdPer, EditText etCarModelPer,Switch switchShowAddress,ScrollView scrollViewAddress,TextView tvCarAddress)
    {
        this.db.collection(Collections.AMBULANCES)
                .whereEqualTo("email", username)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Extract the document ID
                        String documentId = document.getId();

                        // Create a map to store updated fields
                        Map<String, Object> updates = new HashMap<>();


                        updates.put("car_id", etCarIdPer.getText().toString());
                        updates.put("car_model", etCarModelPer.getText().toString());
                        updates.put("is_available", switchShowAddress.isChecked());



                        // Perform the update
                        this.db.collection(Collections.AMBULANCES)
                                .document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UPDATE DATA FIREBASE", "Ambulance data has been successfully updated!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UPDATE DATA FIREBASE", "Error updating ambulance data", e);
                                });
                    }
                });
    }
    private void showLogoutConfirmationDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, id) -> {
                    Intent intent = new Intent(requireActivity(),SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    saveCredentials(username);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void saveCredentials(String email) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        editor.putString("email", email);
        editor.putString("password","123456");
        editor.apply(); // Save new data

    }
}
