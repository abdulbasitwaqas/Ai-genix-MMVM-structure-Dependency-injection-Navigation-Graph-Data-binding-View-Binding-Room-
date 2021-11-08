package com.jsbl.genix.model.maps;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MarkerCameraAnimation {


    private static final double EARTHRADIUS = 6366198;

    @NonNull
    public static MarkerCameraAnimation getInstance() {
        return new MarkerCameraAnimation();
    }

    @NonNull
    public LatLngBounds createBoundsWithMinDiagonal(@NonNull LatLng firstMarker, @NonNull LatLng secondMarker) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(firstMarker);
        builder.include(secondMarker);

        LatLngBounds tmpBounds = builder.build();
        /** Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. */
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = move(center, 700, 709);
        LatLng southWest = move(center, -500, -500);
        builder.include(southWest);
        builder.include(northEast);
        return builder.build();
    }

    /**
     * Create a new LatLng which lies toNorth meters north and toEast meters
     * east of startLL
     */
    @NonNull
    private static LatLng move(@NonNull LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }


    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }


}
