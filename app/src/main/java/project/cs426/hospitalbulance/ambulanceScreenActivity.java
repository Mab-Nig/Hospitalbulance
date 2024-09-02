package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class ambulanceScreenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ambulance_screen);

        Intent intent = getIntent();
        if (intent != null) {
            currentLocation = new LatLng( intent.getDoubleExtra("lat", 0.0)
                    ,intent.getDoubleExtra("long", 0.0));
        }

        Log.d("CURRENT LOCATION", currentLocation.latitude + " " + currentLocation.longitude );

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

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(currentLocation).title(currentLocation.latitude + ", " + currentLocation.longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

    }
}
