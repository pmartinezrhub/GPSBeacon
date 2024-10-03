package com.example.gpsbeacon;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TrackLocation extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 10000;
    protected LocationManager locationManager;
    boolean canGetLocation = false;
    double latitude;
    double longitude;
    String deviceID;
    Location gps_loc;
    Context mContext;

    @SuppressLint("HardwareIds")
    public TrackLocation(Context mContext) {
        this.mContext = mContext;
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        deviceID = getDeviceID();
        gps_loc = getLocation();
    }

    private static String parse_device_data(String deviceId, double latitude, double longitude) {
        String data_parsed;
        data_parsed = deviceId + ":" + latitude + ":" + longitude;
        Log.d("data", data_parsed);
        return data_parsed;
    }

    private String getDeviceID() {
        deviceID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    private boolean checkGPS_enabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private Location getLocation() {
        try {
            // if GPS Enabled get lat/long using GPS Services
            if (checkGPS_enabled()) {
                this.canGetLocation = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (gps_loc != null) {
                        latitude = gps_loc.getLatitude();
                        longitude = gps_loc.getLongitude();
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        try {
            sendPOST(latitude, longitude, deviceID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gps_loc;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Toast.makeText(mContext, "Location has changed", Toast.LENGTH_SHORT).show();
        try {
            sendPOST(latitude, longitude, deviceID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendPOST(double latitude, double longitude, String deviceID) throws IOException {
        Thread post_thread = new Thread() {
            @Override
            public void run() {
                try {
                    String url = "http://mamut.nsupdate.info:8088";
                    String jsonInputString = parse_device_data(deviceID, latitude, longitude);
                    URL urlObj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("response", response.toString());
                        //Toast.makeText(mContext, "sending post: ", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.d("error", e.toString());
                }
            }
        };
        post_thread.start();
    }
}
