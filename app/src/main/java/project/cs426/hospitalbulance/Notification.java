package project.cs426.hospitalbulance;

import android.os.Parcel;
import android.os.Parcelable;

public class Notification implements Parcelable {
    private String typeOfEmergency;
    private String address;

    public Notification(String typeOfEmergency, String address) {
        this.typeOfEmergency = typeOfEmergency;
        this.address = address;
    }

    protected Notification(Parcel in) {
        typeOfEmergency = in.readString();
        address = in.readString();
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
        dest.writeString(typeOfEmergency);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTypeOfEmergency() {
        return typeOfEmergency;
    }

    public String getAddress() {
        return address;
    }
}