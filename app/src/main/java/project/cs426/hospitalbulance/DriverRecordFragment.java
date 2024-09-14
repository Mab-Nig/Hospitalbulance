package project.cs426.hospitalbulance;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import project.cs426.hospitalbulance.backend.database.Ambulance;
import project.cs426.hospitalbulance.backend.database.Call;
import project.cs426.hospitalbulance.backend.database.Collections;
import project.cs426.hospitalbulance.backend.database.Patient;

public class DriverRecordFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username = "";
    private String carID = "";
    private String modelCar = "";
    private String carIDCur = "";
    private boolean canUpdate = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ambulance_record_screen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize components
        //if (getArguments() != null) {
        //    username = getArguments().getString("username");
        //}

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        username = currentUser.getEmail();
        ConstraintLayout screen = view.findViewById(R.id.screen);

        Button save = view.findViewById(R.id.save_button);
        save.setBackgroundColor(Color.parseColor("#00CF00"));

        EditText carIDtext = view.findViewById(R.id.carID);
        EditText carModel = view.findViewById(R.id.carModel);

        db.collection(Collections.AMBULANCES).whereEqualTo("email",username)
                .get().addOnSuccessListener(listResult -> {
                    for( DocumentSnapshot result : listResult.getDocuments())
                    {
                        Log.d("CALL",username);
                        Ambulance car = result.toObject(Ambulance.class);
                        String index = result.getId();
                        carID = car.getCarID();
                        modelCar = car.getCarModel();
                        Log.d("CALLID",carID);

                        carIDtext.setText(carID);


                        carModel.setText(modelCar);

                        setupEditTextFocusListener(carIDtext, "carID");
                        setupEditTextFocusListener(carModel, "carModel");

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(canUpdate) {
                                        Button save = view.findViewById(R.id.save_button);
                                        //ENTERING NEW DATA TO DATABASE
                                        car.setCarID(carID);
                                        car.setCarModel(modelCar);
                                        db.collection(Collections.AMBULANCES).document(index) // Assuming carID is the document ID
                                                .set(car) // Update the entire document with the updated car object
                                                .addOnSuccessListener(aVoid -> {
                                                    // Success!
                                                    save.setBackgroundColor(Color.parseColor("#00CF00")); // Indicate success
                                                    Toast.makeText(view.getContext(), "Ambulance updated successfully!", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle errors
                                                    save.setBackgroundColor(Color.parseColor("#FF0000")); // Indicate error
                                                    Toast.makeText(view.getContext(), "Error updating ambulance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                        //save.setBackgroundColor(Color.parseColor("#00CF00"));
                                        canUpdate = false;
                                    }
                                }

                            });

                    }
                });


        ListView listcall = view.findViewById(R.id.list_call);
        List<String> data_call = new ArrayList<String>();

        readCalldata(data_call,listcall);



        screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopEnter(carIDtext);
                stopEnter(carModel);
                return false;
            }
        });
    }

    private void setupEditTextFocusListener(EditText editText, String type) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String content = editText.getText().toString();
                boolean shouldUpdate = false;
                switch (type) {
                    case "carID":
                        shouldUpdate = !content.equals(carID);
                        carID = content;
                        break;
                    case "carModel":
                        shouldUpdate = !content.equals(modelCar);
                        modelCar = content;
                        break;
                }
                if (shouldUpdate) {
                    Button save = requireView().findViewById(R.id.save_button);
                    save.setBackgroundColor(Color.parseColor("#808080"));
                    canUpdate = true;
                }
            }
        });
    }

    private void stopEnter(EditText editText) {
        if (editText.hasFocus()) {
            editText.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }




    private void readCalldata(List<String> dataCall,ListView listcall) {
        //perform read data from the call that this ambulance hold (using carID)
        //The structure of dataCall:
        //Date1 email1 place1 Date2 email2 place2 ....


        //dataCall.add("3/9/2024");
        //dataCall.add("user2@gmail.com");
       // dataCall.add("HCMUS");

        db.collection(Collections.AMBULANCES).whereEqualTo("email",username)
                .get().addOnSuccessListener(listResult -> {
                    for (DocumentSnapshot result : listResult.getDocuments()) {
                        Log.d("CALL", username);
                        Ambulance car = result.toObject(Ambulance.class);
                        String index = result.getId();
                        carIDCur = car.getCarID();
                        Log.d("CALLIDCUR",carIDCur);
                        this.db.collection(Collections.CALLS)
                                .whereEqualTo("car_id", carIDCur)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot result_1 : querySnapshot.getDocuments()) {
                                        Call call = result_1.toObject(Call.class);
                                        if (call != null) {
                                            //dataCall can  not add in here

                                            Log.d("CALL", call.getAddress());
                                            Log.d("CALL", call.getCallerEmail());
                                            Date timestamp = call.getTimestamp();
                                            // Format Date to string
                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
                                            String formattedDateString = formatter.format(timestamp);
                                           dataCall.add(formattedDateString);
                                            dataCall.add( call.getCallerEmail());
                                            dataCall.add(call.getAddress());
                                        }
                                    }
                                    processDataCall(dataCall);
                                    callAdapter calls = new callAdapter(getContext(), dataCall);
                                    listcall.setAdapter(calls);
                                });
                    }
                });
    }
    // Function to process or display the updated dataCall list
    private void processDataCall(List<String> dataCall) {
        // Process the dataCall list, for example, update a ListView or RecyclerView
        for (int i = 0; i < dataCall.size(); i += 3) {
            Log.d("Processed Call Data", "Date: " + dataCall.get(i) + ", Email: " + dataCall.get(i + 1) + ", Address: " + dataCall.get(i + 2));
        }

        // Example: update the UI or list view here with the new data
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Button save = requireView().findViewById(R.id.save_button);
        if (save != null) {
            save = null;
        }
    }

}

