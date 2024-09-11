package project.cs426.hospitalbulance;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    private String caseType;
    private String address;
    private String ambulanceInfo;
    private String ownerId;
    private String callId;
    private String dispatchId;
    private String hospitalId;
    private String status;
    private long timestamp;  // Store timestamp as long for easier manipulation

    public Notification(String caseType, String address, String ambulanceInfo, String ownerId,
                        String callId, String dispatchId, String hospitalId, String status, long timestamp) {
        this.caseType = caseType;
        this.address = address;
        this.ambulanceInfo = ambulanceInfo;
        this.ownerId = ownerId;
        this.callId = callId;
        this.dispatchId = dispatchId;
        this.hospitalId = hospitalId;
        this.status = status;
        this.timestamp = timestamp;
    }

    protected Notification(Parcel in) {
        caseType = in.readString();
        address = in.readString();
        ambulanceInfo = in.readString();
        ownerId = in.readString();
        callId = in.readString();
        dispatchId = in.readString();
        hospitalId = in.readString();
        status = in.readString();
        timestamp = in.readLong();
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
        dest.writeString(ambulanceInfo);
        dest.writeString(ownerId);
        dest.writeString(callId);
        dest.writeString(dispatchId);
        dest.writeString(hospitalId);
        dest.writeString(status);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCaseType() { return caseType; }
    public String getAddress() { return address; }
    public String getDispatchId() { return dispatchId; }
}