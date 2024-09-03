package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeScreenHomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_REQUEST_CODE = 1000;

    private double currentLat;
    private double currentLong;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_home);
        Button cancel = findViewById(R.id.cancle_button);
        cancel.setTextColor(Color.parseColor("#FFFFFF"));
        cancel.setBackgroundColor(Color.parseColor("#C53434"));

        ImageButton document = findViewById(R.id.document_button);
        document.setOnClickListener(this);
        ImageButton call = findViewById(R.id.call_button);
        call.setOnClickListener(this);
        findViewById(R.id.cancle_button).setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDataAndCall();
            }
        }
    }

    private void getDataAndCall() {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {

              if(location != null) {
                  Toast.makeText(this, "Find your place!", Toast.LENGTH_SHORT).show();
                  currentLong = location.getLongitude();
                  currentLat = location.getLatitude();
                Intent myIntent = new Intent(HomeScreenHomeActivity.this, ambulanceScreenActivity.class);
                myIntent.putExtra("lat", currentLat); //Optional parameters
                myIntent.putExtra("long", currentLong);
                this.startActivity(myIntent);}
              else
              {
                  Toast.makeText(this, "Can not find your place!", Toast.LENGTH_SHORT).show();
              }
            }).addOnFailureListener(this, location -> {
                Toast.makeText(this, "Can not find your place!", Toast.LENGTH_SHORT).show();;
            });
    }


    @Override
    public void onClick(View v) {
        if(v == findViewById(R.id.document_button))
        {
            String username = "Ronaldo";
            Intent myIntent = new Intent(HomeScreenHomeActivity.this, HomeScreenRecordActivity.class);
            myIntent.putExtra("username", username); //Optional parameters
            this.startActivity(myIntent);
        }
        else if (v == findViewById(R.id.call_button))
        {
            ImageView gifImageView = findViewById(R.id.gifImageView);

            // Load GIF using Glide
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.loading)
                    .into(gifImageView);



            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                    getDataAndCall();
            }
        }
        else if(v == findViewById(R.id.cancle_button))
        {
            ImageView gifImageView = findViewById(R.id.gifImageView);
            gifImageView.setVisibility(View.INVISIBLE);
            //handle stop finding car
        }
    }
}
