package com.example.findelectriccarstation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class StationInfos implements Parcelable {
    private String stationName;
    private String stationRegion;
    private double distance;
    private LatLng latLng;


    public StationInfos(String stationName, String stationRegion, double distance, LatLng latLng) {
        this.stationName = stationName;
        this.stationRegion = stationRegion;
        this.distance = distance;
        this.latLng = latLng;
    }

    protected StationInfos(Parcel in) {
        stationName = in.readString();
        stationRegion = in.readString();
        distance = in.readDouble();

    }

    public static final Creator<StationInfos> CREATOR = new Creator<StationInfos>() {
        @Override
        public StationInfos createFromParcel(Parcel source) {
            return new StationInfos(source);
        }

        @Override
        public StationInfos[] newArray(int size) {
            return new StationInfos[size];
        }
    };

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getStationRegion() {
        return stationRegion;
    }

    public void setStationRegion(String stationRegion) {
        this.stationRegion = stationRegion;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stationName);
        dest.writeString(stationRegion);
        dest.writeDouble(distance);
    }
}
