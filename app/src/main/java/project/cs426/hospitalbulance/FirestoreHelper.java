package project.cs426.hospitalbulance;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    // Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void addMultipleCallDocuments() {

        // Loop to create 'count' number of documents
        for (int i = 0; i < 10; i++) {

            // Create a new call document with placeholder values of the correct types
            Map<String, Object> callData = new HashMap<>();
            callData.put("address", "");
            callData.put("adults", 0);
            callData.put("caller_email", "");
            callData.put("car_id", "");
            callData.put("case", "");
            callData.put("children", 0);
            callData.put("hospital_id", "");
            callData.put("is_accepted", false);
            callData.put("location", new GeoPoint(0, 0));
            callData.put("maps_id", "");
            callData.put("process", "");
            callData.put("status", "");
            callData.put("timestamp", Timestamp.now());

            // Add a new document to the "calls" collection
            db.collection("calls")
                    .add(callData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Document added successfully
                            System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                            System.err.println("Error adding document: " + e.getMessage());
                        }
                    });
        }
    }
}