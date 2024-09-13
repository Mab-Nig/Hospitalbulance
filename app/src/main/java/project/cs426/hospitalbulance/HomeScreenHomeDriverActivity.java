package project.cs426.hospitalbulance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenHomeDriverActivity extends AppCompatActivity implements OnMapReadyCallback, View

        .OnClickListener{

    private GoogleMap mMap;

    private String username = "";

    private String callID ="";

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String statusDetail = "";

    private String caseDetail = "";
    private String adultDetail = "1" ;
    private String childrenDetail= "0";

   private int carStatus = 0;
   //0  for available, 1 for on going, 2 for picking patient ,3 for going to hospital, 4 for arriving hospital

    private LatLng currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private final int LOCATION_REQUEST_CODE = 1000;

    private static final String TAG = "Directions";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final String API_KEY = "AIzaSyAI1QP38yYGgMKA2z32ANGztqmd518Pf1Q"; // Replace with your API key
    private static String PLACE_ID_1 = "PLACE_ID_1"; // Replace with your Place ID 1
    private static String PLACE_ID_2 = "PLACE_ID_2"; // Replace with your Place ID 2

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_driver_home);

        Intent getintent = getIntent();
        username = getintent.getStringExtra("username");

        Button car_status = findViewById(R.id.car_status);
        car_status.setBackgroundColor(Color.parseColor("#00CF00"));
        car_status.setOnClickListener(this);

        Button confirm = findViewById(R.id.confirm_button);
        confirm.setBackgroundColor(Color.parseColor("#808080"));
        confirm.setOnClickListener(this);

        Button call = findViewById(R.id.call_button);
        call.setBackgroundColor(Color.parseColor("#808080"));

        findViewById(R.id.document_button).setOnClickListener(this);
        findViewById(R.id.personal_button).setOnClickListener(this);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the map fragment to the container
        fragmentTransaction.add(R.id.map_fragment_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        EditText adultNum = findViewById(R.id.numberOfAdults);
        EditText childNum = findViewById(R.id.numberOfChildren);
        EditText caseEdit = findViewById(R.id.case_edit);
        EditText status = findViewById(R.id.status);

        adultNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(adultNum.getText());
                    if (!content.isEmpty() && !content.equals(adultDetail))
                    {
                        Button cf = findViewById(R.id.confirm_button);
                        cf.setBackgroundColor(Color.parseColor("#808080"));
                        adultDetail = content;
                    }
                }
            }
        });
        childNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(childNum.getText());
                    if (!content.isEmpty() && !content.equals(childrenDetail))
                    {
                        Button cf = findViewById(R.id.confirm_button);
                        cf.setBackgroundColor(Color.parseColor("#808080"));
                        childrenDetail = content;
                    }
                }
            }
        });
        caseEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(caseEdit.getText());
                    if (!content.isEmpty() && !content.equals(caseDetail))
                    {
                        Button cf = findViewById(R.id.confirm_button);
                        cf.setBackgroundColor(Color.parseColor("#808080"));
                        caseDetail = content;
                    }
                }
            }
        });

        status.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText gained focus
                } else {
                    // EditText lost focus
                    String content = String.valueOf(status.getText());
                    if (!content.isEmpty() && !content.equals(statusDetail))
                    {
                        Button cf = findViewById(R.id.confirm_button);
                        cf.setBackgroundColor(Color.parseColor("#808080"));
                        statusDetail = content;
                    }
                }
            }
        });

        ConstraintLayout screen = findViewById(R.id.home_driver_Layout);
        screen.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                // Clear focus from the EditText
                stopEnter(adultNum);
                stopEnter(childNum);
                stopEnter(caseEdit);
                stopEnter(status);
                return false;
            }

            private void stopEnter(EditText adultNum) {
                if (adultNum.hasFocus()) {
                    adultNum.clearFocus();
                    // Optionally, hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(adultNum.getWindowToken(), 0);
                    }
                }
            }
        });
        //Read request, if this car have a request, get the location of Caller



    }

    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;


        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Get last known location and update the marker
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                getPlaceIdFromLocation(location);
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });

        CollectionReference callsRef = firestore.collection("calls");
        Query query1 = callsRef.whereEqualTo("car_id", "59A-11111")
                .whereEqualTo("process", "cancel");

        query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null) {
                    if(value.size() > 1)
                    {
                        //Can be 2 call cancel?
                        // return;
                    }
                    // Process the data from the Firestore snapshot
                    for (DocumentSnapshot doc : value) {
                        Button status = findViewById(R.id.car_status);
                        status.setText("AVAILABLE");
                        status.setBackgroundColor(Color.parseColor("#00CF00"));
                        carStatus = 0;
                        Button call = findViewById(R.id.call_button);
                        call.setBackgroundColor(Color.parseColor("#808080"));
                        DocumentReference cancleCall = doc.getReference();
                        cancleCall.delete();
                        //delete call in database
                        LatLng NullLocation = new LatLng(0, 0);
                        mMap.addMarker(new MarkerOptions().position(NullLocation).title("Call has been cancel"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NullLocation, 15));
                        //set_car_available to true
                    }
                    // Use or display the data as needed
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


        Query query = callsRef.whereEqualTo("car_id", "59A-11111")
                .whereEqualTo("process", "waiting");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null) {
                    if(value.size() > 1)
                    {
                        //Call system error as 1 car is use for 2 call?
                        return;
                    }
                    // Process the data from the Firestore snapshot
                    for (DocumentSnapshot doc : value) {
                        GeoPoint callLocation = doc.getGeoPoint("location");
                        String placeID = doc.getString("userPlaceID");
                        PLACE_ID_2 = placeID;
                        callID  = doc.getId();
                        LatLng PatientLocation = new LatLng(callLocation.getLatitude(), callLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(PatientLocation).title("Patients are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PatientLocation, 15));

                        Button call = findViewById(R.id.call_button);
                        call.setBackgroundColor(Color.parseColor("#00CF00"));
                    }
                    // Use or display the data as needed
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }

    private void getPlaceIdFromLocation(Location location) {
        String latLng = location.getLatitude() + "," + location.getLongitude();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlacesService service = retrofit.create(PlacesService.class);
        Call<PlacesResponse> call = service.getNearbyPlaces(latLng, 50, API_KEY); // Radius 50 meters

        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Result> results = response.body().results;
                    if (!results.isEmpty()) {
                        String placeId = results.get(0).place_id; // Get the first result's Place ID
                        Log.d(TAG, "Place ID: " + placeId);
                        PLACE_ID_1 = placeId;
                        // Use the place ID as needed
                    } else {
                        Log.e(TAG, "No places found near the location.");
                    }
                } else {
                    Log.e(TAG, "Failed to get places: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PlacesResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching places: " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.car_status))
        {
            Button des = findViewById(R.id.car_status);
            if(!Objects.equals(callID, "")) //ONLY CHANGE STATE WHEN HAVE A CALL
            {
                carStatus = (carStatus + 1)%5;
            }
            //0  for available, 1 for on going, 2 for picking patient ,3 for going to hospital, 4 for arriving hospital
            switch (carStatus)
            {
                case 0:
                {
                    des.setText("AVAILABLE");
                    des.setBackgroundColor(Color.parseColor("#00CF00"));
                    updateStatus("FINISH");
                    callID = "";
                    break;
                }
                case 1:
                {
                    des.setText("ON GOING");
                    des.setBackgroundColor(Color.parseColor("#C53434"));
                    updateStatus("ON GOING");
                    break;
                }
                case 2:
                {
                    des.setText("PICKING PATIENT");
                    des.setBackgroundColor(Color.parseColor("#C53434"));
                    updateStatus("PICKING PATIENT");
                    break;
                }
                case 3:
                {
                    des.setText("GO TO HOSPITAL");
                    des.setBackgroundColor(Color.parseColor("#C53434"));
                    updateStatus("GO TO HOSPITAL");
                    break;
                }
                case 4:
                {
                    des.setText("ARRIVE HOSPITAL");
                    des.setBackgroundColor(Color.parseColor("#C53434"));
                    updateStatus("ARRIVE HOSPITAL");
                    break;
                }
            }
        }
        else if(v == findViewById(R.id.confirm_button))
        {
            Button confirm = findViewById(R.id.confirm_button);
            confirm.setBackgroundColor(Color.parseColor("#00CF00"));
        }
        else if(v == findViewById(R.id.document_button))
        {
            Intent myIntent = new Intent(HomeScreenHomeDriverActivity.this, HomeScreenRecordDriverActivity.class);
            String carID = "59A-11111"; //Perform read carID here instead
            myIntent.putExtra("carID", carID);
            this.startActivity(myIntent);
        }
        else if(v == findViewById(R.id.personal_button))
        {
            Intent myIntent = new Intent(HomeScreenHomeDriverActivity.this, AmbulancePersonal.class);
            String carID = "59A-11111"; //Perform read carID here instead
            myIntent.putExtra("carID", carID);
            myIntent.putExtra("username",username);
            this.startActivity(myIntent);
            Log.d("HomeScreenHomeDriverActivity", "Personal button clicked");
        }

    }

    private void updateStatus(String newStatus) {
        if(!Objects.equals(callID, "")) {
            CollectionReference callsRef = firestore.collection("calls");
            DocumentReference docRef = callsRef.document(callID);

            docRef.update("process", newStatus)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Status field updated successfully."))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating status field", e));


        }


    }
}
