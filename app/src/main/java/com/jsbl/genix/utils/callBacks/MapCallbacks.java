package com.jsbl.genix.utils.callBacks;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface MapCallbacks {

    void onMapReady(GoogleMap googleMap);

    void onCameraIdle();

    void onCameraMove();

    void onMapClick(LatLng latLng);

    boolean onMarkerClick(Marker marker);

    void onLocationSuccess(Location location);

}
