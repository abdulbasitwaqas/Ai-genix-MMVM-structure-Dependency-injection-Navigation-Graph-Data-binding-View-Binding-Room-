package com.jsbl.genix.utils.callBacks;


import com.google.android.gms.maps.model.LatLng;

public interface AddressListener {
    void startLoading(boolean load);

    void onGetAddress(String title, String address, String subAddress, String placeId);

    void onGetLocation(LatLng latLng);
}
