
package com.jsbl.genix.model.maps;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationDetail implements Parcelable {

    @Nullable
    @SerializedName("_id")
    @Expose
    private String id;
    @Nullable
    @SerializedName("name")
    @Expose
    private String title = "";
    @Nullable
    @SerializedName("address")
    @Expose
    private String address = "";
    @SerializedName("lat")
    @Expose
    private double lat = 0.0;
    @SerializedName("lng")
    @Expose
    private double lng = 0.0;

    @Nullable
    @SerializedName("place_id")
    @Expose
    private String placeId = "";

    /**
     * No args constructor for use in serialization
     */
    public LocationDetail() {
    }

    /**
     * @param id
     * @param title
     * @param address
     * @param lng
     * @param lat
     */
    public LocationDetail(String id, String title, String address, double lat, double lng) {
        super();
        this.id = id;
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }


    public LocationDetail(String id, String title, String address, double lat, double lng, String placeId) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.placeId = placeId;
    }

    public LocationDetail(double lat, double lng) {
        this.id = "";
        this.title = "";
        this.address = "";
        this.lat = lat;
        this.lng = lng;
        this.placeId = "";
    }

    public LocationDetail(String fp_address, String s, Double fp_latitude, Double fp_longitude) {
        this.address = fp_address;
        this.title =  s;
        this.lat = fp_latitude;
        this.lng = fp_longitude;
    }

    @NonNull
    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public LocationDetail withId(String id) {
        this.id = id;
        return this;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    public LocationDetail withTitle(String title) {
        this.title = title;
        return this;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NonNull
    public LocationDetail withAddress(String address) {
        this.address = address;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @NonNull
    public LocationDetail withLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @NonNull
    public LocationDetail withLng(double lng) {
        this.lng = lng;
        return this;
    }

    @Nullable
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.address);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.placeId);
    }

    protected LocationDetail(@NonNull Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.address = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.placeId = in.readString();
    }

    public static final Creator<LocationDetail> CREATOR = new Creator<LocationDetail>() {
        @NonNull
        @Override
        public LocationDetail createFromParcel(@NonNull Parcel source) {
            return new LocationDetail(source);
        }

        @NonNull
        @Override
        public LocationDetail[] newArray(int size) {
            return new LocationDetail[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", placeId='" + placeId + '\'' +
                '}';
    }
}
