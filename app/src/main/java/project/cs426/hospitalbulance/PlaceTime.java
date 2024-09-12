package project.cs426.hospitalbulance;

public class PlaceTime {

    private int duration;
    private String MapID;

    public PlaceTime(String id, int time)
    {
        duration = time;
        MapID = id;
    }

    public String getMapID() {
        return MapID;
    }

    public void setMapID(String mapID) {
        MapID = mapID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
