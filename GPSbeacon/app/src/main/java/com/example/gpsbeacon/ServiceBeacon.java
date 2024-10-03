package com.example.gpsbeacon;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceBeacon extends Service implements LocationListener {

    public double latitude = 0;
    public double longitude = 0;
    public String deviceID;
    private LocationManager locationManager;
    public static final String ACTION_UPDATE_DEVICE_ID = "com.example.gpsbeacon.UPDATE_DEVICE_ID";
    public static final String EXTRA_DEVICE_ID = "com.example.gpsbeacon.EXTRA_DEVICE_ID";
    public static final String ACTION_START_SAVING = "com.example.gpsbeacon.ACTION_START_SAVING";
    private URLManager urlManager; // Declarar URLManager
    private Boolean save_route = false;
    //private String filename;
    private String filenamegjson;

    private static String parse_device_data(String deviceId, double latitude, double longitude) {
        String data_parsed;
        data_parsed = deviceId + ":" + latitude + ":" + longitude;
        Log.d("data", data_parsed);
        return data_parsed;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        deviceID = getDeviceID();
        urlManager = new URLManager(getApplicationContext());
        String url = urlManager.loadURL();

        if (url == null) {
            //url = "http://pmartinezr.h4ck.me:8088";
            url = "http://192.168.0.20:8088";
            urlManager.saveURL(url);
        }
        try {
            sendPOST(latitude, longitude, deviceID, url); // Pasa la URL cargada al método sendPOST
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Log.d("Service", "Started Service");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Need permissions", Toast.LENGTH_LONG).show();
        } else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this::onLocationChanged);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_UPDATE_DEVICE_ID.equals(intent.getAction())) {
            String newDeviceID = intent.getStringExtra(EXTRA_DEVICE_ID);
            if (newDeviceID != null) {
                deviceID = newDeviceID;
                Toast.makeText(getApplicationContext(), "Device ID updated: " + deviceID, Toast.LENGTH_SHORT).show();
                Log.d("ServiceBeacon", "Device ID updated to: " + deviceID);
            }
            else {
                deviceID = getDeviceID();
            }
        }
        // Manejo de la acción para guardar la ruta (ACTION_START_SAVING)
        if (ACTION_START_SAVING.equals(intent.getAction())) {
            save_route = true;
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;
            //filename = ("route" + dateFormat.format(date) + ".csv") ;
            filenamegjson = ("route-" + dateFormat.format(date) + ".json") ;
        }

        return START_STICKY;
    }


    private String getDeviceID() {
        deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    private void updateGPSCoords(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }
    private void saveRouteAsCSV(String data, String filename,Context context) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND)) {
                fos.write((data + "\n").getBytes());
            }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void saveRouteAsGeoJSON(String deviceID, double latitude, double longitude, String filename, Context context) {
        try {
            JSONObject geoJson;
            JSONArray featuresArray;

            // Leer el archivo existente (si ya tiene datos guardados)
            String existingContent = readGeoJSONFile(filename, context);
            if (existingContent != null && !existingContent.isEmpty()) {
                geoJson = new JSONObject(existingContent);
                featuresArray = geoJson.getJSONArray("features");
            } else {
                // Si el archivo está vacío o no existe, crear la estructura base de GeoJSON
                geoJson = new JSONObject();
                geoJson.put("type", "FeatureCollection");
                featuresArray = new JSONArray();
            }

            // Crear un nuevo punto GeoJSON
            JSONObject newFeature = new JSONObject();
            newFeature.put("type", "Feature");

            // Crear la geometría del punto
            JSONObject geometry = new JSONObject();
            geometry.put("type", "Point");
            JSONArray coordinates = new JSONArray();
            coordinates.put(longitude);
            coordinates.put(latitude);
            geometry.put("coordinates", coordinates);

            // Crear las propiedades adicionales del punto
            JSONObject properties = new JSONObject();
            properties.put("deviceID", deviceID);

            // Añadir geometría y propiedades a la nueva característica
            newFeature.put("geometry", geometry);
            newFeature.put("properties", properties);

            // Añadir el nuevo punto a la lista de características
            featuresArray.put(newFeature);
            geoJson.put("features", featuresArray);

            // Guardar el contenido actualizado en el archivo
            try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
                fos.write(geoJson.toString(4).getBytes());  // toString(4) agrega sangría para que sea más legible
            }
            Log.d("GeoJSON", "Data successfully saved in GeoJSON format!");

        } catch (JSONException | IOException e) {
            Log.e("GeoJSON", "Error creating GeoJSON: " + e.toString());
        }
    }

    private String readGeoJSONFile(String filename, Context context) {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = context.openFileInput(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();

        } catch (IOException e) {
            Log.e("GeoJSON", "Error reading GeoJSON file: " + e.toString());
            return null; // Si no existe, devolver null para indicar que es un archivo nuevo
        }
    }


    private void sendPOST(double latitude, double longitude, String deviceID, String url) throws IOException {
        Thread post_thread = new Thread() {
            @Override
            public void run() {
                try {
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
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("response", response.toString());
                    }
                } catch (IOException e) {
                    Log.d("error", e.toString());
                }
            }
        };
        post_thread.start();
        Toast.makeText(getApplicationContext(), "POST sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();
        updateGPSCoords(location);
        String url = urlManager.loadURL(); // Cargar la URL guardada
        try {
            sendPOST(latitude, longitude, deviceID, url);
            if (save_route == true){
                String data =  deviceID + "," + latitude + "," + longitude;
                //saveRouteAsCSV(data, filename, getApplicationContext());
                saveRouteAsGeoJSON(deviceID, latitude, longitude, filenamegjson, getApplicationContext());
                Toast.makeText(getApplicationContext(), "Data saved: " + data, Toast.LENGTH_SHORT).show();
                Log.d("ServiceBeacon", "Data saved: " + data);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
