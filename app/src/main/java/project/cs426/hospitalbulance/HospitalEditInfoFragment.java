package project.cs426.hospitalbulance;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;

public class HospitalEditInfoFragment extends Fragment {

    private FirebaseFirestore db;
    private EditText hospitalNameEditText, hospitalAddressEditText;
    private String userEmail;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_edit_info, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        hospitalNameEditText = view.findViewById(R.id.hospitalNameEditText);
        hospitalAddressEditText = view.findViewById(R.id.hospitalAddressEditText);
        Button saveInfoButton = view.findViewById(R.id.saveInfoButton);
        ImageButton logoutButton = view.findViewById(R.id.logoutButton);
        TextView logoutTextView = view.findViewById(R.id.logoutTextView);

        // Assume the userEmail is passed via Bundle (from the parent activity)
        userEmail = getArguments() != null ? getArguments().getString("username") : null;

        // Check if userEmail is valid
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "User email not found!", Toast.LENGTH_LONG).show();
            return view;
        }

        // Fetch hospital info in real-time
        listenForHospitalInfo(userEmail);

        // Handle save button click
        saveInfoButton.setOnClickListener(v -> saveHospitalInfo(userEmail));

        // Handle logout button click
        logoutButton.setOnClickListener(v -> logout());
        logoutTextView.setOnClickListener(v -> logout());

        return view;
    }

    private void listenForHospitalInfo(String email) {
        db.collection("hospitals")
                .whereEqualTo("email", email)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("HospitalEditInfoFragment", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            Map<String, Object> info = (Map<String, Object>) document.get("info");
                            if (info != null) {
                                String name = (String) info.get("name");
                                String address = (String) info.get("address");

                                hospitalNameEditText.setText(name);
                                hospitalAddressEditText.setText(address);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No hospital information found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveHospitalInfo(String email) {
        String updatedName = hospitalNameEditText.getText().toString().trim();
        String updatedAddress = hospitalAddressEditText.getText().toString().trim();

        if (TextUtils.isEmpty(updatedName)) {
            hospitalNameEditText.setError("Hospital name is required.");
            hospitalNameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(updatedAddress)) {
            hospitalAddressEditText.setError("Address is required.");
            hospitalAddressEditText.requestFocus();
            return;
        }

        db.collection("hospitals")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        document.getReference().update("info.name", updatedName, "info.address", updatedAddress)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Hospital info updated successfully.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating hospital info.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Hospital not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        if (mAuth != null) {
            mAuth.signOut();
            Intent logoutIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(logoutIntent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else {
            // Handle the case where mAuth is null
            System.err.println("FirebaseAuth instance is null.");
        }
    }
}