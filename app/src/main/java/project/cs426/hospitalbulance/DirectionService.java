package project.cs426.hospitalbulance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionService {
    @GET("directions/json")
    Call<DirectionsResponse> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode, // Set to 'driving', 'walking', 'bicycling', 'transit', or 'two_wheeler'
            @Query("key") String apiKey
    );
}
