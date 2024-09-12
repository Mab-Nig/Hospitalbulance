package project.cs426.hospitalbulance;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HospitalWithDurations {

    private static final String API_KEY = "AIzaSyAI1QP38yYGgMKA2z32ANGztqmd518Pf1Q"; // Replace with your API key

    private int num;

    private  String startMapID;

    private static final String TAG = "Directions";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";

    private ArrayList<PlaceTime> hospitalTimes = new ArrayList<>() ; //in second
    public HospitalWithDurations(String startID, PlaceTimesCallback callback)
    {
        this.startMapID = startID;
        FirebaseFirestore db  = FirebaseFirestore.getInstance();
        CollectionReference hospitals = db.collection("hospitals");
        hospitals.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Loop through each document snapshot
                    num = task.getResult().size();
                    for (DocumentSnapshot document : task.getResult()) {
                        // Access data from each document
                        String hospitalsMapID = (String) document.get("mapID");
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        DirectionService service = retrofit.create(DirectionService.class);
                        Call<DirectionsResponse> call = service.getDirections("place_id:" + startID, "place_id:" + hospitalsMapID, "driving", API_KEY);

                        call.enqueue(new Callback<DirectionsResponse>() {
                            @Override
                            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    DirectionsResponse directionsResponse = response.body();
                                    if (directionsResponse.routes != null && !directionsResponse.routes.isEmpty()) {
                                        Leg leg = directionsResponse.routes.get(0).legs.get(0);
                                        String durationText = leg.duration.text;
                                        hospitalTimes.add(new PlaceTime(hospitalsMapID,  leg.duration.value));
                                        Log.d("Hospital add", "count: " + hospitalTimes.size());
                                        // Display durationText in your UI
                                        // Notify that the data is ready
                                        if(hospitalTimes.size() == num) {
                                            callback.onPlaceTimesReady(hospitalTimes);
                                        }
                                    } else {
                                        callback.onError("No routes found");
                                    }
                                } else {
                                    callback.onError("Failed to get directions: " + response.message());
                                }
                            }

                            @Override
                            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                                Log.e(TAG, "Error fetching directions: " + t.getMessage());
                            }
                        });
                    }

                } else {
                    // Handle any errors
                    System.err.println("Error getting documents: " + task.getException());
                }
            }

        });
    }

    public ArrayList<PlaceTime> getHospitalTimes()
    {
        return hospitalTimes;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
