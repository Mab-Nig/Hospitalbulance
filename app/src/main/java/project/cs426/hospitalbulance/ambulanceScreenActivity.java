package project.cs426.hospitalbulance;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ambulanceScreenActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private String USER_PLACE_NAME;
    private GoogleMap mMap;
    private LatLng currentLocation;

    private FirebaseFirestore db;

    private ArrayList<PlaceTime> listHospitalTimes ;

    private ArrayList<PlaceTime> listAmbulanceTimes;

    private boolean is_call = false;


    private static final String TAG = "Directions";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private String USER_PLACE_ID;

    private final String API_KEY = "AIzaSyAI1QP38yYGgMKA2z32ANGztqmd518Pf1Q";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ambulance_screen);

        Intent intent = getIntent();
        if (intent != null) {
            currentLocation = new LatLng(intent.getDoubleExtra("lat", 0.0)
                    , intent.getDoubleExtra("long", 0.0));
            USER_PLACE_ID = intent.getStringExtra("placeID");
            USER_PLACE_NAME = intent.getStringExtra("PlaceName");

        }

        Log.d("CURRENT LOCATION", currentLocation.latitude + " " + currentLocation.longitude);

        Button mask = findViewById(R.id.mask_search_place);
        mask.setBackgroundColor(Color.parseColor("#FFFFFF"));


        TextView place = findViewById(R.id.user_place);
        place.setText("Your place: " + USER_PLACE_NAME);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the map fragment to the container
        fragmentTransaction.add(R.id.map_fragment_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //UI fix
        Button call_car = findViewById(R.id.call_car_button);
        call_car.setBackgroundColor(Color.parseColor("#C53434"));

        Button call_taxi = findViewById(R.id.call_taxi);
        call_taxi.setBackgroundColor(Color.parseColor("#FFE434"));

        findViewById(R.id.backArrowButton).setOnClickListener(this);

        ImageView gifImageView = findViewById(R.id.gifImageView);

        // Load GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .into(gifImageView);
        TextView car_content = findViewById(R.id.car_finding_content);
        car_content.setText("");

        gifImageView.setVisibility(View.INVISIBLE);

        // Set up the AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle place selection
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    USER_PLACE_ID = place.getId();
                    USER_PLACE_NAME = place.getName();
                    TextView myplace = findViewById(R.id.user_place);
                    myplace.setText("Your place: " + USER_PLACE_NAME);
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle errors
                Log.e("MainActivity", "An error occurred: " + status);
            }
        });
        call_car.setOnClickListener(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(currentLocation).title(USER_PLACE_NAME));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.call_car_button)) {
            if(!is_call) {
                is_call = true;
                Button call_car = findViewById(R.id.call_car_button);
                call_car.setText("CANCEL");

                ImageView gifImageView = findViewById(R.id.gifImageView);
                gifImageView.setVisibility(View.VISIBLE);

                // make a request to server using USER_PLACE_ID and USER_PLACE_NAME
                // When finding car, update car ID, car model, ambulance owner

                //DETAIL:
                //PERFORM READING all hospital + AMBULANCE places

                AmbulanceWithDurations findAmbulance = new AmbulanceWithDurations(USER_PLACE_ID, new PlaceTimesCallback() {
                    @Override
                    public void onPlaceTimesReady(ArrayList<PlaceTime> placeTimes) {
                        listAmbulanceTimes = placeTimes;
                        // Can access mapID and duration to go to a hospital: ex hospitalTimes.get(0).get
                        listAmbulanceTimes.sort(Comparator.comparingInt(PlaceTime::getDuration));
                        //Already sort from fastest to lowest route to different ambulances

                        Log.d("CHECK SIZE", "onPlaceTimesReady: " + listAmbulanceTimes.size());
                        // Print sorted list for verification
                        for (PlaceTime ht : listAmbulanceTimes) {
                            Log.d("CHECK SORT AMBULANCE", "Duration in sec " + ht.getDuration());
                        }

                        //PERFORM SENDING REQUEST FINDING HOSPITAL AVAILABLE ON SYSTEM WITH FASTEST ROUTE DUE TO ListAmbulanceTimes

                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle the error case
                        Log.e(TAG, "Error: " + errorMessage);
                    }
                });
                HospitalWithDurations findHospital = new HospitalWithDurations(USER_PLACE_ID, new PlaceTimesCallback() {
                    @Override
                    public void onPlaceTimesReady(ArrayList<PlaceTime> placeTimes) {
                        listHospitalTimes = placeTimes;
                        // Can access mapID and duration to go to a hospital: ex hospitalTimes.get(0).get
                        listHospitalTimes.sort(Comparator.comparingInt(PlaceTime::getDuration));
                        //Already sort from fastest to lowest route to different hospitals
                        // Print sorted list for verification

                        for (PlaceTime ht : listHospitalTimes) {
                            Log.d("CHECK SORT HOSPITAL", "Duration in sec " + ht.getDuration());
                        }

                        //PERFORM SENDING REQUEST FINDING HOSPITAL AVAILABLE ON SYSTEM WITH FASTEST ROUTE DUE TO ListHospitalTimes
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Handle the error case
                        Log.e(TAG, "Error: " + errorMessage);
                    }
                });
            }
            else {
                is_call = false;
                Button call_car = findViewById(R.id.call_car_button);
                call_car.setText("CALL");

                ImageView gifImageView = findViewById(R.id.gifImageView);
                gifImageView.setVisibility(View.INVISIBLE);


                //LOOK FOR THE CALL AND SET THE PROCESS TO CANCEL
            }
        }
        else if (v == findViewById(R.id.backArrowButton))
        {
            onBackPressed();
        }
    }
}



