package project.cs426.hospitalbulance;

import java.util.ArrayList;

public interface HospitalTimesCallback {
    void onHospitalTimesReady(ArrayList<HospitalTime> hospitalTimes);
    void onError(String errorMessage);
}
