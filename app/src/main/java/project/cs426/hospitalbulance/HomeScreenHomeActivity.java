package project.cs426.hospitalbulance;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenHomeActivity extends AppCompatActivity implements View.OnClickListener {
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_REQUEST_CODE = 1000;


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

        // Set up Cancel button
        Button cancel = findViewById(R.id.cancel_button);
        cancel.setTextColor(Color.parseColor("#FFFFFF"));
        cancel.setBackgroundColor(Color.parseColor("#C53434"));

        // Set up Document button
        ImageButton document = findViewById(R.id.document_button);
        document.setOnClickListener(this);

        ImageButton call = findViewById(R.id.call_button);
        call.setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
        findViewById(R.id.personal_button).setOnClickListener(this);

        // Set up Fractures button
        ImageButton fracturesButton = findViewById(R.id.fractures_button);
        fracturesButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Fractures");
            intent.putExtra("content", "•\tStop any bleeding. Apply pressure to the wound with a sterile bandage, a clean cloth or a clean piece of clothing.\n" +
                    "•\tKeep the injured area from moving. Don't try to realign the bone or push a bone that's sticking out back in. If you've been trained in how to splint and medical help isn't available right away, apply a splint to the area above and below the fracture sites. Padding the splints can help reduce pain.\n" +
                    "•\tApply ice packs to limit swelling and help relieve pain. Don't apply ice directly to the skin. Wrap the ice in a towel, a piece of cloth or some other material.\n" +
                    "•\tTreat for shock. If the person feels faint or is breathing in short, rapid breaths, lay the person down with the head slightly lower than the trunk. If you can, raise the legs.\n");
            intent.putExtra("imageResId", R.drawable.fractures);
            startActivity(intent);
        });

        // Set up Breathing button
        ImageButton breathingButton = findViewById(R.id.breath_button);
        breathingButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Breathing");
            intent.putExtra("content", "•\tCheck the person's airway, breathing, and pulse. If necessary, begin CPR.\n" +
                    "•\tLoosen any tight clothing.\n" +
                    "•\tHelp the person use any prescribed medicine (such as an asthma inhaler or home oxygen).\n" +
                    "•\tContinue to monitor the person's breathing and pulse until medical help arrives. DO NOT assume that the person's condition is improving if you can no longer hear abnormal breath sounds, such as wheezing.\n" +
                    "•\tIf there are open wounds in the neck or chest, they must be closed immediately, especially if air bubbles appear in the wound. Bandage such wounds at once.\n" +
                    "•\tA \"sucking\" chest wound allows air to enter the person's chest cavity with each breath. This can cause a collapsed lung. Bandage the wound with plastic wrap, a plastic bag, or gauze pads covered with petroleum jelly, sealing it on three sides, leaving one side unsealed. This creates a valve to prevent air from entering the chest through the wound, while allowing trapped air to escape from the chest through the unsealed side.\n");
            intent.putExtra("imageResId", R.drawable.breathing);
            startActivity(intent);
        });

        // Set up Nausea button
        ImageButton nauseaButton = findViewById(R.id.nausea_button);
        nauseaButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Nausea");
            intent.putExtra("content", "At-home nausea remedies may include:\n" +
                    "•\tDrinking clear and/or ice-cold drinks.\n" +
                    "•\tEating light, bland foods (such as saltine crackers or plain bread).\n" +
                    "•\tAvoiding fried, greasy or sweet foods.\n" +
                    "•\tEating slowly and eating smaller, more frequent meals.\n" +
                    "•\tNot mixing hot and cold foods.\n" +
                    "•\tDrinking beverages slowly.\n" +
                    "•\tAvoiding activity after eating.\n" +
                    "•\tAvoiding brushing your teeth after eating.\n" +
                    "•\tChoosing foods from all the food groups as you can tolerate them to get adequate nutrition.\n" +
                    "Treatment for vomiting includes:\n" +
                    "•\tDrinking gradually larger amounts of clear liquids.\n" +
                    "•\tAvoiding solid food until the vomiting episode has passed.\n" +
                    "•\tResting.\n");
            intent.putExtra("imageResId", R.drawable.nausea);
            startActivity(intent);
        });

        // Set up CPR button
        ImageButton cprButton = findViewById(R.id.cpr_button);
        cprButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "CPR");
            intent.putExtra("content", "Follow these CPR instructions to help someone older than an infant:\n" +
                    "•\tIf the person isn’t breathing, put one of your hands over the other and place them in the middle of the person’s chest (right under their nipples). If you’re helping a child up to age 8, use one hand and place it right above the bottom of their breastbone.\n" +
                    "•\tPutting the force of your body weight behind it, push your hands down hard in the middle of the person’s chest. Use the heel of your hand, or the part just before your wrist. Keep your arms straight.\n" +
                    "•\tKeep pushing on the person’s chest (chest compressions) 100 to 120 times per minute, pushing down 2 inches (about the height or short side of a credit card) each time. Make sure you allow their chest to come all the way back up between compressions.\n" +
                    "•\tPeople who have CPR training can pause compressions to give the person two mouth-to-mouth rescue breaths for every 30 compressions (about 20 seconds or so).\n" +
                    "•\tKeep doing chest compressions and giving rescue breaths in a cycle until the person revives or more help arrives.\n" +
                    "Perform the rescue breath as follows:\n" +
                    "•\tPinch the person’s nose closed while tilting their head back a little and their chin up.\n" +
                    "•\tClose your mouth over theirs and blow a normal-sized breath into it so their chest goes up. If the person’s chest doesn’t come up, check to see if there’s something in their mouth.\n" +
                    "•\tGive a total of two breaths and go back to doing compressions.\n" +
                    "While you’re doing CPR, someone should be bringing an AED to use to help with resuscitating the person.\n");
            intent.putExtra("imageResId", R.drawable.cpr);
            startActivity(intent);
        });

        // Set up Narcotics button
        ImageButton narcoticsButton = findViewById(R.id.narcotis_button);
        narcoticsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Narcotics");
            intent.putExtra("content", "•\tCheck the person's airway, breathing, and pulse. If needed, begin CPR. If unconscious but breathing, carefully place the person in the recovery position by log rolling the person toward you onto their left side. Bend the top leg so both hip and knee are at right angles. Gently tilt their head back to keep the airway open. If the person is conscious, loosen the clothing and keep the person warm, and provide reassurance. Try to keep the person calm. If you suspect an overdose, try to prevent the person from taking more drugs. Call for medical help right away.\n" +
                    "•\tTreat the person for signs of shock. Signs include weakness, bluish lips and fingernails, clammy skin, paleness, and decreasing alertness.\n" +
                    "•\tIf the person is having seizures, give first aid for seizures.\n" +
                    "•\tKeep monitoring the person's vital signs (pulse, rate of breathing, blood pressure, if possible) until emergency medical help arrives.\n" +
                    "•\tIf possible, try to determine which drug(s) were taken, how much and when. Save any pill bottles or other drug containers. Give this information to emergency personnel.\n");
            intent.putExtra("imageResId", R.drawable.narcotics);
            startActivity(intent);
        });

        // Set up Radiation button
        ImageButton radiationButton = findViewById(R.id.radiation_button);
        radiationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenHomeActivity.this, FirstAidCaseActivity.class);
            intent.putExtra("title", "Radiation");
            intent.putExtra("content", "•\tCheck the person's breathing and pulse.\n" +
                    "•\tStart CPR, if necessary.\n" +
                    "•\tRemove the person's clothing and place the items in a sealed container. This stops ongoing contamination.\n" +
                    "•\tVigorously wash the victim with soap and water.\n" +
                    "•\tDry the victim and wrap with a soft, clean blanket.\n" +
                    "•\tCall for emergency medical help or take the person to nearest emergency medical facility if you can do so safely.\n" +
                    "•\tReport the exposure to emergency officials.\n" +
                    "If symptoms occur during or after medical radiation treatments:\n" +
                    "•\tTell the provider or seek medical treatment right away.\n" +
                    "•\tHandle affected areas gently.\n" +
                    "•\tTreat symptoms or illnesses as recommended by the provider.\n");
            intent.putExtra("imageResId", R.drawable.radiation);
            startActivity(intent);
        });
        findViewById(R.id.personal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_per = new Intent(HomeScreenHomeActivity.this, HomeScreenPersonalActivity.class);

                startActivity(intent_per);
            }
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
                Toast.makeText(this, "Can not find your place1!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, location -> {
            Toast.makeText(this, "Can not find your place2!", Toast.LENGTH_SHORT).show();;
        });
    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.document_button)) {
            String username = "user@gmail.com";
            Intent myIntent = new Intent(HomeScreenHomeActivity.this, HomeScreenRecordActivity.class);
            myIntent.putExtra("username", username); // Optional parameters
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
        else if(v == findViewById(R.id.cancel_button))
        {
            ImageView gifImageView = findViewById(R.id.gifImageView);
            gifImageView.setVisibility(View.INVISIBLE);
            //handle stop finding car
        }
        else if(v == findViewById(R.id.personal_button))
        {
            Intent intent = new Intent(this, HomeScreenPersonalActivity.class);
            this.startActivity(intent);
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
                        Intent myIntent = new Intent(HomeScreenHomeActivity.this, ambulanceScreenActivity.class);
                        myIntent.putExtra("placeID", USER_PLACE_ID);
                        myIntent.putExtra("PlaceName", results.get(0).name);
                        myIntent.putExtra("lat", currentLat); //Optional parameters
                        myIntent.putExtra("long", currentLong);
                        startActivity(myIntent);
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
