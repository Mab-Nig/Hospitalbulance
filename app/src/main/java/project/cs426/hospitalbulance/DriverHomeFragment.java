package project.cs426.hospitalbulance;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import project.cs426.hospitalbulance.backend.database.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DriverHomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean canUpdate = false;

    private String carID ="";
    private String username = "";
    private String callID = "";
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private String statusDetail = "";
    private String caseDetail = "";
    private String adultDetail = "1";
    private String childrenDetail = "0";

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

    private ListenerRegistration WaittingListenerRegistration;
    private ListenerRegistration CancleListenerRegistration;

    private ListenerRegistration HospitalListenerRegistration;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen_driver_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize components
        if (getArguments() != null) {
            username = getArguments().getString("username");
            Log.d("READ EMAIL", "onViewCreated: " + username);
        }



        Button car_status = view.findViewById(R.id.car_status);
        car_status.setBackgroundColor(Color.parseColor("#00CF00"));


        car_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button des = view.findViewById(R.id.car_status);
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
        });

        Button confirm = view.findViewById(R.id.confirm_button);
        confirm.setBackgroundColor(Color.parseColor("#00CF00"));


        Button call = view.findViewById(R.id.call_button);
        call.setBackgroundColor(Color.parseColor("#808080"));


        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().add(R.id.map_fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        EditText adultNum = view.findViewById(R.id.numberOfAdults);
        EditText childNum = view.findViewById(R.id.numberOfChildren);
        EditText caseEdit = view.findViewById(R.id.case_edit);
        EditText status = view.findViewById(R.id.status);

        setupEditTextFocusListener(adultNum, "adult");
        setupEditTextFocusListener(childNum, "children");
        setupEditTextFocusListener(caseEdit, "case");
        setupEditTextFocusListener(status, "status");

        ConstraintLayout screen = view.findViewById(R.id.home_driver_Layout);
        screen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopEnter(adultNum);
                stopEnter(childNum);
                stopEnter(caseEdit);
                stopEnter(status);
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
                    case "adult":
                        shouldUpdate = !content.equals(adultDetail);
                        adultDetail = content;
                        break;
                    case "children":
                        shouldUpdate = !content.equals(childrenDetail);
                        childrenDetail = content;
                        break;
                    case "case":
                        shouldUpdate = !content.equals(caseDetail);
                        caseDetail = content;
                        break;
                    case "status":
                        shouldUpdate = !content.equals(statusDetail);
                        statusDetail = content;
                        break;
                }
                if (shouldUpdate) {
                    Button cf = requireView().findViewById(R.id.confirm_button);
                    cf.setBackgroundColor(Color.parseColor("#808080"));
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                getPlaceIdFromLocation(location);
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        });

        db.collection(Collections.AMBULANCES).whereEqualTo("email",username)
                .get().addOnSuccessListener(listResult ->{
                    for(DocumentSnapshot result : listResult)
                    {
                        carID =  result.getString("car_id");
                        CollectionReference callsRef = firestore.collection(Collections.CALLS);
                        Query query1 = callsRef.whereEqualTo("car_id", carID)
                                .whereEqualTo("process", "cancel");

                        CancleListenerRegistration = query1.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        Button status = requireView().findViewById(R.id.car_status);
                                        status.setText("AVAILABLE");
                                        status.setBackgroundColor(Color.parseColor("#00CF00"));
                                        carStatus = 0;
                                        Button call =requireView().findViewById(R.id.call_button);
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


                        Query query = callsRef.whereEqualTo("car_id", carID)
                                .whereEqualTo("process", "waiting");

                        WaittingListenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        project.cs426.hospitalbulance.backend.database.Call aCall
                                                = doc.toObject(project.cs426.hospitalbulance.backend.database.Call.class);
                                        String placeID = doc.getString("maps_id");
                                        PLACE_ID_2 = placeID;
                                        callID  = doc.getId();
//
                                        Places.initialize(requireContext(), API_KEY);

// Create a new PlacesClient instance
                                        PlacesClient placesClient = Places.createClient(requireContext());

// Use the Place ID of the location you want to add a marker to
                                        String placeId = PLACE_ID_2;  // Replace with the actual Place ID

// Define a list of the place details you need (we need the LatLng)
                                        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

// Fetch the Place details using the Place ID
                                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                                        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                            Place place = response.getPlace();
                                            LatLng location = place.getLatLng();  // Get the location from the place

                                            if (location != null) {
                                                // Add a marker to the map at the location of the place
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(location)
                                                        .title("Patient: "+response.getPlace().getName()));
                                                // Optionally move and zoom the camera to the location
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                                            }
                                        }).addOnFailureListener((exception) -> {
                                            if (exception instanceof ApiException) {
                                                ApiException apiException = (ApiException) exception;
                                                Log.e("MapsActivity", "Place not found: " + apiException.getMessage());
                                            }
                                        });
                                        Button call = requireView().findViewById(R.id.call_button);
                                        call.setBackgroundColor(Color.parseColor("#00CF00"));

                                        Button confirm = requireView().findViewById(R.id.confirm_button);
                                        confirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(canUpdate && !Objects.equals(callID, ""))
                                                {
                                                    aCall.setStatus(statusDetail);
                                                    aCall.setAdults(Integer.valueOf(adultDetail));
                                                    aCall.setChildren(Integer.valueOf(childrenDetail));
                                                    aCall.setCases(caseDetail);
                                                    db.collection(Collections.CALLS).document(callID).set(aCall);
                                                }
                                            }
                                        });
                                    }
                                    // Use or display the data as needed
                                } else {
                                    Log.d(TAG, "Current data: null");
                                }
                            }
                        });


                        //CollectionReference callsRef = db.collection(Collections.CALLS);
                        Query query2 = callsRef.whereEqualTo("car_id", carID)
                                .whereEqualTo("process", "PICKING PATIENT").whereEqualTo("is_accepted", true);
                        HospitalListenerRegistration = query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                        //Log.d("CHECK FOR HOSPITAL ACCEPT", "onEvent: " );
                                        String placeID = doc.getString("hospital_id");
                                        PLACE_ID_2 = placeID;
                                        callID  = doc.getId();

                                        Places.initialize(requireContext(), API_KEY);

// Create a new PlacesClient instance
                                        PlacesClient placesClient = Places.createClient(requireContext());

// Use the Place ID of the location you want to add a marker to
                                        String placeId = PLACE_ID_2;  // Replace with the actual Place ID

// Define a list of the place details you need (we need the LatLng)
                                        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

// Fetch the Place details using the Place ID
                                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                                        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                            Place place = response.getPlace();
                                            LatLng location = place.getLatLng();  // Get the location from the place

                                            if (location != null) {
                                                // Add a marker to the map at the location of the place
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(location)
                                                        .title("Hospital: "+response.getPlace().getName()));
                                                // Optionally move and zoom the camera to the location
                                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                                            }
                                        }).addOnFailureListener((exception) -> {
                                            if (exception instanceof ApiException) {
                                                ApiException apiException = (ApiException) exception;
                                                Log.e("MapsActivity", "Place not found: " + apiException.getMessage());
                                            }
                                        });
                                    }
                                    // Use or display the data as needed
                                } else {
                                    Log.d(TAG, "Current data: null");
                                }
                            }
                        });


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
    private void updateStatus(String newStatus) {
        if(!Objects.equals(callID, "")) {
            CollectionReference callsRef = firestore.collection("calls");
            DocumentReference docRef = callsRef.document(callID);

            docRef.update("process", newStatus)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Status field updated successfully."))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating status field", e));

        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
           // mMap.setMyLocationEnabled(false);
            mMap.clear();
            mMap = null;
        }
        // Remove listener for Query
        if (WaittingListenerRegistration != null) {
            WaittingListenerRegistration.remove();
            WaittingListenerRegistration = null;
        }

        // Remove listener for Query
        if (CancleListenerRegistration != null) {
            CancleListenerRegistration.remove();
            CancleListenerRegistration = null;
        }

        if (HospitalListenerRegistration != null) {
            HospitalListenerRegistration.remove();
            HospitalListenerRegistration = null;
        }
    }
}
