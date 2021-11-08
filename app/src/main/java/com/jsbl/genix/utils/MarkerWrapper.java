package com.jsbl.genix.utils;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jsbl.genix.R;
import com.jsbl.genix.model.maps.LocationDetail;
import com.jsbl.genix.model.maps.LocationDetailHandler;

import java.util.ArrayList;
import java.util.List;

public class MarkerWrapper {
    Context context;
    GoogleMap mMap;
    @NonNull
    List<LocationDetailHandler> pickLocationDetailHandlers = new ArrayList<>();
    @NonNull
    List<LocationDetailHandler> dropOffLocationDetailHandlers = new ArrayList<>();
    GroundOverlay rippleCircle;

//    ArrayList<DriverInformation> drivers = new ArrayList<>();

    //TAGs
    public static final String TAG_DRIVER = "driver";
    public static final String TAG_FREE_RIDE = "freeRide";
    public static final String TAG_PICK_UP = "pickup";
    public static final String TAG_DROP_OFF = "dropOff";

    public int filterValue = 0;
    private final int RIPPLE_DURATION = 700;


    //Handler
    Handler mainHandler;

    HandlerThread handlerThread;
    Looper looper;

    Handler pickupHandler;
    Handler dropOffHandler;
    Handler freeRideHandler;
    Handler driverHandler;


    public MarkerWrapper(GoogleMap mMap, @NonNull Context context) {
        this.mMap = mMap;
        this.context = context;
        mainHandler = new Handler(context.getMainLooper());
        handlerThread = new HandlerThread("pickupHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();
        pickupHandler = new Handler(looper);

        handlerThread = new HandlerThread("dropOffHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();
        dropOffHandler = new Handler(looper);

        handlerThread = new HandlerThread("freeRideHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();
        freeRideHandler = new Handler(looper);

        handlerThread = new HandlerThread("driverHandlerThread");
        handlerThread.start();
        looper = handlerThread.getLooper();
        driverHandler = new Handler(looper);
    }


    public void handlePickupLocationMarkers(@NonNull LocationDetail locationDetail, int index) {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                }
                if (index < pickLocationDetailHandlers.size()) {
                    updatePickupMarker(locationDetail, index);
                } else {
                    createPickupMarker(locationDetail);

                }
            }
        });
    }

    public void handleDropOffLocationMarkers(@NonNull LocationDetail locationDetail, int index) {
        dropOffHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dropOffLocationDetailHandlers.size() == 0) {
                    dropOffLocationDetailHandlers.clear();
                }
                if (index < dropOffLocationDetailHandlers.size()) {
                    updateDropOffMarker(locationDetail, index);
                } else {
                    createDropOffMarker(locationDetail);

                }
            }
        });
    }

    private void createPickupMarker(@NonNull LocationDetail locationDetail) {
        if (mMap == null) {
            return;
        }
        LocationDetailHandler locationDetailHandler = new LocationDetailHandler();
        locationDetailHandler.setLocationDetail(locationDetail);

        Bitmap bitmap = getPickMarker();
        if (bitmap == null) {
            return;
        }
        mainHandler.post(() -> {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDetail.getLat(), locationDetail.getLng())).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).draggable(false));
            marker.setTag(TAG_PICK_UP + ":");
            locationDetailHandler.setMarker(marker);
        });
        pickLocationDetailHandlers.add(locationDetailHandler);
    }

    private void updatePickupMarker(@NonNull LocationDetail locationDetail, int index) {
        if (this.pickLocationDetailHandlers.size() == 0) {
            pickLocationDetailHandlers.clear();
            return;
        }
        if (index < pickLocationDetailHandlers.size()) {
            pickLocationDetailHandlers.get(index).setLocationDetail(locationDetail);
            mainHandler.post(() -> {
                if (pickLocationDetailHandlers.size() == 0) {
                    return;
                }
                if (index < pickLocationDetailHandlers.size()) {
                    pickLocationDetailHandlers.get(index).getMarker().setPosition(new LatLng(locationDetail.getLat(), locationDetail.getLng()));
                }
            });
        }
    }


    public void hideAllPickupLocations() {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                for (int i = 0; i < pickLocationDetailHandlers.size(); i++) {
                    final int post = i;
                    mainHandler.post(() -> {
                        if (pickLocationDetailHandlers.size() == 0) {
                            return;
                        }
                        if (post < pickLocationDetailHandlers.size()) {
                            pickLocationDetailHandlers.get(post).getMarker().setVisible(false);
                        }
                    });
                }
            }
        });
    }

    public void hideAllPickupLocationsExceptFirst() {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                for (int i = 0; i < pickLocationDetailHandlers.size(); i++) {
                    final int pos = i;
                    if (i != 0)
                        mainHandler.post(() -> {
                            if (pos < pickLocationDetailHandlers.size()) {
                                pickLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                            }
                        });
                }
            }
        });
    }

    public void showAllPickupLocations() {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                for (int i = 0; i < pickLocationDetailHandlers.size(); i++) {
                    final int pos = i;
                    mainHandler.post(() -> {
                        if (pos < pickLocationDetailHandlers.size()) {
                            pickLocationDetailHandlers.get(pos).getMarker().setVisible(true);
                        }
                    });
                }
            }
        });
    }

    public void removeAllPickupMarkers() {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                for (int i = pickLocationDetailHandlers.size() - 1; i >= 0; i--) {
                    final int pos = i;
                    mainHandler.post(() -> {
                        if (pickLocationDetailHandlers.size() == 0) {
                            return;
                        }
                        if (pos < pickLocationDetailHandlers.size()) {
                            pickLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                            pickLocationDetailHandlers.get(pos).getMarker().remove();
                            pickLocationDetailHandlers.remove(pos);
                        }
                    });
                }
            }
        });
    }

    public void removeAllPickupMarkersExceptFirst() {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                for (int i = pickLocationDetailHandlers.size() - 1; i >= 0; i--) {
                    final int pos = i;
                    if (i != 0) {
                        mainHandler.post(() -> {
                            if (pickLocationDetailHandlers.size() == 0) {
                                return;
                            }
                            if (pos < pickLocationDetailHandlers.size()) {
                                pickLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                                pickLocationDetailHandlers.get(pos).getMarker().remove();
                                pickLocationDetailHandlers.remove(pos);
                            }
                        });
                    }
                }
            }
        });
    }

    private void removePickupMarker(int index) {
        pickupHandler.post(new Runnable() {
            @Override
            public void run() {
                if (pickLocationDetailHandlers.size() == 0) {
                    pickLocationDetailHandlers.clear();
                    return;
                }
                if (index < pickLocationDetailHandlers.size()) {
                    mainHandler.post(() -> {
                        if (pickLocationDetailHandlers.size() == 0) {
                            return;
                        }
                        if (index < pickLocationDetailHandlers.size()) {
                            pickLocationDetailHandlers.get(index).getMarker().setVisible(false);
                            pickLocationDetailHandlers.get(index).getMarker().remove();
                            pickLocationDetailHandlers.remove(index);
                        }
                    });
                }
            }
        });
    }


    private void createDropOffMarker(@NonNull LocationDetail locationDetail) {
        if (mMap == null) {
            return;
        }
        LocationDetailHandler locationDetailHandler = new LocationDetailHandler();
        locationDetailHandler.setLocationDetail(locationDetail);
        Bitmap bitmap = getDropMarker();
        if (bitmap == null) {
            return;
        }
        mainHandler.post(() -> {
            Marker marker;
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(locationDetail.getLat(), locationDetail.getLng())).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).draggable(false));
            marker.setTag(TAG_DROP_OFF + ":");
            locationDetailHandler.setMarker(marker);
        });
        dropOffLocationDetailHandlers.add(locationDetailHandler);
    }

    private void updateDropOffMarker(@NonNull LocationDetail locationDetail, int index) {
        if (this.dropOffLocationDetailHandlers.size() == 0) {
            dropOffLocationDetailHandlers.clear();
            return;
        }
        if (index < dropOffLocationDetailHandlers.size()) {
            dropOffLocationDetailHandlers.get(index).setLocationDetail(locationDetail);
            mainHandler.post(() -> {
                if (index < dropOffLocationDetailHandlers.size()) {
                    dropOffLocationDetailHandlers.get(index).getMarker().setPosition(new LatLng(locationDetail.getLat(), locationDetail.getLng()));
                }
            });
        }
    }


    public void hideAllDropOffLocations() {
        dropOffHandler.post(() -> {
            if (dropOffLocationDetailHandlers.size() == 0) {
                dropOffLocationDetailHandlers.clear();
                return;
            }
            for (int i = 0; i < dropOffLocationDetailHandlers.size(); i++) {
                final int pos = i;
                mainHandler.post(() -> {
                            if (pos < dropOffLocationDetailHandlers.size()) {
                                dropOffLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                            }
                        }
                );
            }
        });
    }

    public void hideAllDropOffLocationsExceptFirst() {

        dropOffHandler.post(() -> {
            if (dropOffLocationDetailHandlers.size() == 0) {
                dropOffLocationDetailHandlers.clear();
                return;
            }
            for (int i = 0; i < dropOffLocationDetailHandlers.size(); i++) {
                final int pos = i;
                if (i != 0)
                    mainHandler.post(() -> {
                                if (pos < dropOffLocationDetailHandlers.size()) {
                                    dropOffLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                                }
                            }
                    );
            }
        });
    }

    public void showAllDropOffLocations() {
        dropOffHandler.post(() -> {
            if (dropOffLocationDetailHandlers.size() == 0) {
                dropOffLocationDetailHandlers.clear();
                return;
            }
            for (int i = 0; i < dropOffLocationDetailHandlers.size(); i++) {
                final int pos = i;
                mainHandler.post(() -> {
                            if (pos < dropOffLocationDetailHandlers.size()) {
                                dropOffLocationDetailHandlers.get(pos).getMarker().setVisible(true);
                            }
                        }
                );
            }
        });
    }

    public void removeAllDropOffMarkers() {
        dropOffHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dropOffLocationDetailHandlers.size() == 0) {
                    dropOffLocationDetailHandlers.clear();
                    return;
                }
                for (int i = dropOffLocationDetailHandlers.size() - 1; i >= 0; i--) {
                    final int pos = i;
                    mainHandler.post(() -> {
                        if (dropOffLocationDetailHandlers.size() == 0) {
                            return;
                        }
                        if (pos < dropOffLocationDetailHandlers.size()) {
                            dropOffLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                            dropOffLocationDetailHandlers.get(pos).getMarker().remove();
                            dropOffLocationDetailHandlers.remove(pos);
                        }
                    });
                }
            }
        });
    }

    public void removeAllDropOffMarkersExceptFirst() {
        dropOffHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dropOffLocationDetailHandlers.size() == 0) {
                    dropOffLocationDetailHandlers.clear();
                    return;
                }
                for (int i = dropOffLocationDetailHandlers.size() - 1; i >= 0; i--) {
                    if (i != 0) {
                        final int pos = i;
                        mainHandler.post(() -> {
                            if (dropOffLocationDetailHandlers.size() == 0) {
                                return;
                            }
                            if (pos < dropOffLocationDetailHandlers.size()) {
                                dropOffLocationDetailHandlers.get(pos).getMarker().setVisible(false);
                                dropOffLocationDetailHandlers.get(pos).getMarker().remove();
                                dropOffLocationDetailHandlers.remove(pos);
                            }
                        });
                    }
                }
            }
        });
    }

    private void removeDropOffMarker(int index) {
        dropOffHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dropOffLocationDetailHandlers.size() == 0) {
                    dropOffLocationDetailHandlers.clear();
                    return;
                }
                if (index < dropOffLocationDetailHandlers.size()) {
                    mainHandler.post(() -> {
                        if (dropOffLocationDetailHandlers.size() == 0) {
                            return;
                        }
                        if (index < dropOffLocationDetailHandlers.size()) {
                            dropOffLocationDetailHandlers.get(index).getMarker().setVisible(false);
                            dropOffLocationDetailHandlers.get(index).getMarker().remove();
                            dropOffLocationDetailHandlers.remove(index);
                        }

                    });
                }
            }
        });
    }

    @Nullable
    public LocationDetail searchPickUp(Marker marker) {
        if (this.pickLocationDetailHandlers.size() == 0) {
            pickLocationDetailHandlers.clear();
            return null;
        }
        for (int i = 0; i < pickLocationDetailHandlers.size(); i++) {
            if (pickLocationDetailHandlers.get(i).getMarker().equals(marker)) {
                return pickLocationDetailHandlers.get(i).getLocationDetail();
            }
        }
        return null;
    }

    @Nullable
    public LocationDetail searchDropOff(Marker marker) {
        if (this.dropOffLocationDetailHandlers.size() == 0) {
            dropOffLocationDetailHandlers.clear();
            return null;
        }
        for (int i = 0; i < dropOffLocationDetailHandlers.size(); i++) {
            if (dropOffLocationDetailHandlers.get(i).getMarker().equals(marker)) {
                return dropOffLocationDetailHandlers.get(i).getLocationDetail();
            }
        }
        return null;
    }

    @Nullable
    public Bitmap loadBitmapFromView(@Nullable View v) {

        if (v == null) {
            return null;
        }
        if (v.getMeasuredHeight() <= 0) {
            v.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        return null;
    }

    @Nullable
    private Bitmap getDriverMarker(int type) {
      /*  int height, width;
        BitmapDrawable bitmapdraw = null;
        if (context == null) {
            return null;
        }
        switch (type) {
            case Constants.MINI:
                height = (int) context.getResources().getDimension(R.dimen._33sdp);
                width = (int) context.getResources().getDimension(R.dimen._23sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carmarker_0);
                break;
            case Constants.BIKE:
                height = (int) context.getResources().getDimension(R.dimen._50sdp);
                width = (int) context.getResources().getDimension(R.dimen._30sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bike_0);
                break;
            case Constants.TAXI:

                height = (int) context.getResources().getDimension(R.dimen._28sdp);
                width = (int) context.getResources().getDimension(R.dimen._22sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.taxi_0);
                break;
            case Constants.GO:

                height = (int) context.getResources().getDimension(R.dimen._33sdp);
                width = (int) context.getResources().getDimension(R.dimen._23sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.sedan_0);
                break;
            case Constants.RICKSHAW:

                height = (int) context.getResources().getDimension(R.dimen._30sdp);
                width = (int) context.getResources().getDimension(R.dimen._25sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.rickshaw_0);
                break;
            case Constants.BURAQX:

                height = (int) context.getResources().getDimension(R.dimen._32sdp);
                width = (int) context.getResources().getDimension(R.dimen._24sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.buraq_x);
                break;
            case Constants.BOLAN:

                height = (int) context.getResources().getDimension(R.dimen._25sdp);
                width = (int) context.getResources().getDimension(R.dimen._25sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bolan_top);
                break;
            case Constants.PICKUP:

                height = (int) context.getResources().getDimension(R.dimen._25sdp);
                width = (int) context.getResources().getDimension(R.dimen._25sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.pickup_top);
                break;
            default:

                height = (int) context.getResources().getDimension(R.dimen._30sdp);
                width = (int) context.getResources().getDimension(R.dimen._23sdp);
                bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.carmarker_0);
                break;
        }
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);*/
        return null;
    }


    @Nullable
    Bitmap getPickMarker() {
        if (context == null) {
            return null;
        }
        int height = (int) context.getResources().getDimension(R.dimen._30sdp);
        int width = (int) context.getResources().getDimension(R.dimen._22sdp);

        BitmapDrawable bitmapdraw;
        bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.end);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }
    @Nullable
    Bitmap getCorneringMarker() {
        if (context == null) {
            return null;
        }
        int height = (int) context.getResources().getDimension(R.dimen._30sdp);
        int width = (int) context.getResources().getDimension(R.dimen._22sdp);

        BitmapDrawable bitmapdraw;
        bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.end);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    @Nullable
    Bitmap getDropMarker() {
        if (context == null) {
            return null;
        }
//        int height = (int) context.getResources().getDimension(R.dimen._20sdp);
//        int width = (int) context.getResources().getDimension(R.dimen._20sdp);

        int height = (int) context.getResources().getDimension(R.dimen._30sdp);
        int width = (int) context.getResources().getDimension(R.dimen._22sdp);

        BitmapDrawable bitmapdraw;
        bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.start);
        Bitmap b = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    @Nullable
    Bitmap getBitmapFromVector(@NonNull Drawable drawable) {
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    public void showRipples(@Nullable LatLng latLng) {
        if (latLng == null) return;
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(500, 500);
        d.setColor(context.getResources().getColor(R.color.cmOnBackground));
        d.setStroke(0, Color.TRANSPARENT);

        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        // Radius of the circle
        final int radius = 200;
        mainHandler.post(() -> {
            if (rippleCircle != null) {
                rippleCircle.setVisible(true);
                return;
            }
            // Add the circle to the map
            rippleCircle = mMap.addGroundOverlay(new GroundOverlayOptions()
                    .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

            // Prep the animator
            PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 0, radius);
            PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);

            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setValues(radiusHolder, transparencyHolder);
            valueAnimator.setDuration(RIPPLE_DURATION);
            valueAnimator.setEvaluator(new FloatEvaluator());
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                    float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
                    float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
                    rippleCircle.setDimensions(animatedRadius * 2);
                    rippleCircle.setTransparency(animatedAlpha);
                }
            });

            // start the animation
            valueAnimator.start();
        });
    }

    public void removeRipples() {
        mainHandler.post(() -> {
            if (rippleCircle != null) rippleCircle.setVisible(false);
        });
    }


}
