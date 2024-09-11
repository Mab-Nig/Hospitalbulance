package project.cs426.hospitalbulance;

import java.util.ArrayList;

public interface PlaceTimesCallback {
    void onPlaceTimesReady(ArrayList<PlaceTime> placeTimes);
    void onError(String errorMessage);
}
