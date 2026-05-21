package co.edu.unipiloto.aplicaciondestiondecombustibles.UI.usuario;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OdometerService extends Service {

    public static final String PREFS_NAME = "odometer_prefs";
    public static final String KEY_TOTAL_METERS = "total_meters";
    public static final String KEY_SESSIONS = "sessions";

    public interface Listener {
        void onUpdate(double sessionMeters, Location location);
        void onStatusChanged(boolean tracking);
    }

    public class LocalBinder extends Binder {
        public OdometerService getService() {
            return OdometerService.this;
        }
    }

    private final IBinder binder = new LocalBinder();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Listener listener;

    private boolean tracking = false;
    private float minDistanceMeters = 10f;
    private long intervalMs = 5000L;
    private double sessionMeters = 0.0;
    private long sessionStart = 0L;
    private String sessionDestination = "";
    private Location lastLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                Location location = locationResult.getLastLocation();
                if (location == null) return;
                if (lastLocation != null) {
                    sessionMeters += lastLocation.distanceTo(location);
                }
                lastLocation = location;
                if (listener != null) {
                    listener.onUpdate(sessionMeters, location);
                }
            }
        };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (listener != null) {
            listener.onStatusChanged(false);
        }
        return super.onUnbind(intent);
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public boolean startTracking(float precisionMeters, long intervalSeconds, @Nullable String destino) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        minDistanceMeters = Math.max(1f, precisionMeters);
        intervalMs = Math.max(1000L, intervalSeconds * 1000L);
        sessionMeters = 0.0;
        sessionStart = System.currentTimeMillis();
        sessionDestination = destino == null ? "" : destino;
        lastLocation = null;

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                .setMinUpdateDistanceMeters(minDistanceMeters)
                .build();
        fusedLocationClient.requestLocationUpdates(request, locationCallback, getMainLooper());
        tracking = true;
        if (listener != null) {
            listener.onStatusChanged(true);
        }
        return true;
    }

    public void stopTracking() {
        if (!tracking) return;
        fusedLocationClient.removeLocationUpdates(locationCallback);
        tracking = false;
        persistSession();
        if (listener != null) {
            listener.onStatusChanged(false);
        }
    }

    public boolean isTracking() {
        return tracking;
    }

    public double getSessionMeters() {
        return sessionMeters;
    }

    public double getTotalMeters() {
        return getTotalMeters(this);
    }

    public static double getTotalMeters(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long bits = prefs.getLong(KEY_TOTAL_METERS, Double.doubleToLongBits(0.0));
        return Double.longBitsToDouble(bits);
    }

    private void persistSession() {
        if (sessionStart == 0L) return;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        double total = getTotalMeters(this) + sessionMeters;
        prefs.edit().putLong(KEY_TOTAL_METERS, Double.doubleToLongBits(total)).apply();

        String existing = prefs.getString(KEY_SESSIONS, "[]");
        JSONArray sessions;
        try {
            sessions = new JSONArray(existing);
        } catch (JSONException e) {
            sessions = new JSONArray();
        }

        JSONObject entry = new JSONObject();
        try {
            entry.put("inicio", sessionStart);
            entry.put("fin", System.currentTimeMillis());
            entry.put("metros", sessionMeters);
            entry.put("destino", sessionDestination);
        } catch (JSONException ignored) {
        }
        sessions.put(entry);
        prefs.edit().putString(KEY_SESSIONS, sessions.toString()).apply();
        sessionStart = 0L;
    }
}
