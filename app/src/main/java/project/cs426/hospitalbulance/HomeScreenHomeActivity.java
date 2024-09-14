package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenHomeActivity extends AppCompatActivity implements View.OnClickListener {


    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_REQUEST_CODE = 1000;

    private boolean isCancel = false;

    private String username = "";
    private double currentLat;
    private double currentLong;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static final String TAG = "Directions";

    private String USER_PLACE_ID;

    private static final String API_KEY = "AIzaSyAI1QP38yYGgMKA2z32ANGztqmd518Pf1Q";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_home);

        Intent getintent = getIntent();
        username = getintent.getStringExtra("username");

        // Set up Cancel button
        Button cancel = findViewById(R.id.cancle_button);
        cancel.setTextColor(Color.parseColor("#FFFFFF"));
        cancel.setBackgroundColor(Color.parseColor("#C53434"));

        // Set up Document button
        ImageButton document = findViewById(R.id.document_button);
        document.setOnClickListener(this);

        ImageButton call = findViewById(R.id.call_button);
        call.setOnClickListener(this);
        findViewById(R.id.cancle_button).setOnClickListener(this);
        findViewById(R.id.personal_button).setOnClickListener(this);

        // Set up Fractures button
        ImageButton fracturesButton = findViewById(R.id.fractures_button);
        fracturesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Fractures");

            String fracturesContent = "First Aid Steps for Fractures:\n\n" +
                    "1. **Stop Bleeding:**\n" +
                    "   • Apply pressure with a sterile bandage or clean cloth.\n\n" +
                    "2. **Immobilize the Injury:**\n" +
                    "   • Avoid moving the injured area. Do not try to realign the bone.\n" +
                    "   • If trained, apply a splint above and below the fracture. Use padding to reduce pain.\n\n" +
                    "3. **Apply Ice Packs:**\n" +
                    "   • Wrap the ice in a towel before applying to reduce swelling.\n\n" +
                    "4. **Treat for Shock:**\n" +
                    "   • Lay the person down with the head slightly lower than the trunk and raise the legs if possible.\n";

            intent.putExtra("content", fracturesContent);
            intent.putExtra("imageResId", R.drawable.fractures);
            startActivity(intent);
        });

        // Set up Breathing button
        ImageButton breathingButton = findViewById(R.id.breath_button);
        breathingButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Breathing");

            String breathingContent = "First Aid for Breathing Issues:\n\n" +
                    "1. **Check Airway, Breathing, and Pulse:**\n" +
                    "   • If necessary, begin CPR immediately.\n\n" +
                    "2. **Loosen Tight Clothing:**\n" +
                    "   • Help the person breathe more easily by loosening their clothing.\n\n" +
                    "3. **Administer Prescribed Medicine:**\n" +
                    "   • Assist the person in using any inhalers or oxygen.\n\n" +
                    "4. **Monitor Breathing and Pulse:**\n" +
                    "   • Keep an eye on the person's condition until help arrives.\n\n" +
                    "5. **Bandage Chest Wounds:**\n" +
                    "   • Apply bandages to open chest wounds to prevent lung collapse.\n";

            intent.putExtra("content", breathingContent);
            intent.putExtra("imageResId", R.drawable.breathing);
            startActivity(intent);
        });

        // Set up Nausea button
        ImageButton nauseaButton = findViewById(R.id.nausea_button);
        nauseaButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Nausea");

            String nauseaContent = "At-Home Remedies for Nausea:\n\n" +
                    "1. **Drinks:**\n" +
                    "   • Drink clear and ice-cold liquids slowly.\n\n" +
                    "2. **Food:**\n" +
                    "   • Eat light, bland foods like saltine crackers or plain bread.\n" +
                    "   • Avoid fried, greasy, or sweet foods.\n" +
                    "   • Eat smaller, more frequent meals instead of large meals.\n\n" +
                    "3. **Rest:**\n" +
                    "   • Rest after meals and avoid physical activity.\n\n" +
                    "4. **Avoid Mixing Foods:**\n" +
                    "   • Don't mix hot and cold foods in the same meal.\n\n" +
                    "Treatment for Vomiting:\n\n" +
                    "1. **Clear Liquids:**\n" +
                    "   • Drink clear liquids in small amounts, increasing gradually.\n\n" +
                    "2. **Avoid Solid Foods:**\n" +
                    "   • Wait until vomiting stops before introducing solid foods.\n\n" +
                    "3. **Rest:**\n" +
                    "   • Get plenty of rest until symptoms improve.\n";

            intent.putExtra("content", nauseaContent);
            intent.putExtra("imageResId", R.drawable.nausea);
            startActivity(intent);
        });

        // Set up CPR button
        ImageButton cprButton = findViewById(R.id.cpr_button);
        cprButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "CPR");

            String cprContent = "CPR Instructions for Adults and Children:\n\n" +
                    "1. **Chest Compressions:**\n" +
                    "   • Place your hands in the center of the chest and press down 2 inches deep, 100-120 times per minute.\n" +
                    "   • Let the chest rise completely between compressions.\n\n" +
                    "2. **Rescue Breaths (If Trained):**\n" +
                    "   • Give two rescue breaths after every 30 compressions.\n" +
                    "   • Pinch the nose, tilt the head, and blow into the mouth until the chest rises.\n\n" +
                    "3. **Continue Until Help Arrives:**\n" +
                    "   • Keep performing CPR until the person starts breathing or help arrives.\n";

            intent.putExtra("content", cprContent);
            intent.putExtra("imageResId", R.drawable.cpr);
            startActivity(intent);
        });

        // Set up Narcotics button
        ImageButton narcoticsButton = findViewById(R.id.narcotis_button);
        narcoticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Narcotics Overdose");

            String narcoticsContent = "First Aid for Narcotics Overdose:\n\n" +
                    "1. **Check Vital Signs:**\n" +
                    "   • Check airway, breathing, and pulse. Start CPR if needed.\n" +
                    "   • Place the person in the recovery position if they are unconscious but breathing.\n\n" +
                    "2. **Prevent Further Drug Use:**\n" +
                    "   • Keep the person calm and prevent them from taking more drugs.\n\n" +
                    "3. **Treat for Shock:**\n" +
                    "   • Look for signs of shock such as pale skin, bluish lips, and weak pulse. Treat accordingly.\n\n" +
                    "4. **Give Seizure First Aid:**\n" +
                    "   • If the person is having seizures, ensure their safety by providing appropriate seizure first aid.\n\n" +
                    "5. **Monitor and Report:**\n" +
                    "   • Monitor vital signs and provide details of the drug(s) taken to emergency personnel.\n";

            intent.putExtra("content", narcoticsContent);
            intent.putExtra("imageResId", R.drawable.narcotics);
            startActivity(intent);
        });

        // Set up Radiation button
        ImageButton radiationButton = findViewById(R.id.radiation_button);
        radiationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Radiation Exposure");

            String radiationContent = "First Aid for Radiation Exposure:\n\n" +
                    "1. **Check Vital Signs:**\n" +
                    "   • Check breathing and pulse. Start CPR if necessary.\n\n" +
                    "2. **Remove Contaminated Clothing:**\n" +
                    "   • Remove and seal clothing in a container to stop further contamination.\n\n" +
                    "3. **Wash with Soap and Water:**\n" +
                    "   • Vigorously wash the victim with soap and water to remove contaminants.\n\n" +
                    "4. **Seek Medical Help:**\n" +
                    "   • Call for emergency medical assistance or transport the person to a medical facility.\n\n" +
                    "5. **Report Exposure:**\n" +
                    "   • Report the exposure to emergency officials immediately.\n";

            intent.putExtra("content", radiationContent);
            intent.putExtra("imageResId", R.drawable.radiation);
            startActivity(intent);
        });


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
                  Toast.makeText(this, "Find your place! ", Toast.LENGTH_SHORT).show();

                  currentLong = location.getLongitude();
                  currentLat = location.getLatitude();

                  getPlaceIdFromLocation(location);
                }
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
        if (v == findViewById(R.id.document_button)) {
            Intent myIntent = new Intent(HomeScreenHomeActivity.this, HomeScreenRecordActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            myIntent.putExtra("username", username); // Optional parameters
            this.startActivity(myIntent);
        }
        else if (v == findViewById(R.id.call_button))
        {
            ImageView gifImageView = findViewById(R.id.gifImageView);
            isCancel = false;

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
            isCancel = true;

        }
        else if(v == findViewById(R.id.personal_button))
        {
            Intent myIntent_personal = new Intent(HomeScreenHomeActivity.this, HomeScreenPersonalActivity.class);
            myIntent_personal.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            myIntent_personal.putExtra("username", username); // Optional parameters
            this.startActivity(myIntent_personal);
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
                        USER_PLACE_ID = placeId;
                        if (!isCancel)
                        {
                            Intent myIntent = new Intent(HomeScreenHomeActivity.this, ambulanceScreenActivity.class);
                            myIntent.putExtra("placeID", USER_PLACE_ID);
                            myIntent.putExtra("PlaceName", results.get(0).name);
                            myIntent.putExtra("lat", currentLat); //Optional parameters
                            myIntent.putExtra("long", currentLong);
                            startActivity(myIntent);
                        }
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
}
