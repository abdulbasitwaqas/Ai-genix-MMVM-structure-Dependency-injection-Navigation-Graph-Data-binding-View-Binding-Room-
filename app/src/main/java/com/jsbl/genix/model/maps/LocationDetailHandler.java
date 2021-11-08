package com.jsbl.genix.model.maps;

import com.google.android.gms.maps.model.Marker;

public class LocationDetailHandler {
    LocationDetail locationDetail;
    Marker marker;


    public LocationDetailHandler(LocationDetail locationDetail, Marker marker) {
        this.locationDetail = locationDetail;
        this.marker = marker;
    }

    public LocationDetailHandler() {
        this.locationDetail = locationDetail;
        this.marker = marker;
    }

    public LocationDetail getLocationDetail() {
        return locationDetail;
    }

    public void setLocationDetail(LocationDetail driver) {
        this.locationDetail = driver;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
