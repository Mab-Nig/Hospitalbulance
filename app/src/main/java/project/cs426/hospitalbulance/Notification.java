package project.cs426.hospitalbulance;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    private String caseType;
    private String address;
    private String carID;
    private String status;
    private long timestamp;  // Store timestamp as long for easier manipulation
    private String callId;

    public Notification(String caseType, String address, String carID, String status, long timestamp, String callId) {
        this.caseType = caseType;
        this.address = address;
        this.carID = carID;
        this.status = status;
        this.timestamp = timestamp;
        this.callId = callId;
    }

    protected Notification(Parcel in) {
        caseType = in.readString();
        address = in.readString();
        carID = in.readString();
        status = in.readString();
        timestamp = in.readLong();
        callId = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caseType);
        dest.writeString(address);
        dest.writeString(carID);
        dest.writeString(status);
        dest.writeLong(timestamp);
        dest.writeString(callId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public String getCaseType() { return caseType; }
    public String getAddress() { return address; }
    public String getCarID() { return carID; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getCallId() { return callId; }
}