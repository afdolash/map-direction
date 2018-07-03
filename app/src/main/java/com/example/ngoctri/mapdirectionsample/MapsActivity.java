package com.example.ngoctri.mapdirectionsample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private static final int REQUEST_LOCATION = 1;
    private static final long DELAY_PEROID = 5000;
    ArrayList<LatLng> listPoints;
    LocationManager locationManager;
    String lattitude,longitude;
    ArrayList<Steps> stepslist;
    int radius=10;
    int indexNextTurn =1;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvStatus = findViewById(R.id.tv_status);
        listPoints = new ArrayList<>();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        setMarker();

    }

    private void setMarker() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //Reset marker when already 2
                if (listPoints.size() == 2) {
                    listPoints.clear();
                    mMap.clear();
                }
                //Save first point select
                listPoints.add(latLng);
                //Create marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                if (listPoints.size() == 1) {
                    //Add first marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else {
                    //Add second marker to the map
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);

                if (listPoints.size() == 2) {
                    //Create the URL to get request from first marker to second marker
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                    taskRequestDirections.execute(url);

                    loopLocation();
                }
            }
        });
    }

    private void loopLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("MISSING","GPS_PROVIDER");

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("SUCCESS","TIMER");
            Timer timerObj = new Timer();
            TimerTask timerTaskObj = new TimerTask() {
                public void run() {
                    getLocation();
                    if (stepslist!=null){
                        countDistance();
////                        Double b = -7.45 + 54.352662;
////
////                        Log.d("b", String.valueOf(b));
////                        Log.d("size lat", String.valueOf(stepslist.get(0).getEnd_location().getLat()));
////                        Log.d("current location",
////                                "Lattitude = " + lattitude+ "\n" +
////                                        "Longitude = " + longitude);
                    }
                }
            };
            timerObj.schedule(timerTaskObj, 0,DELAY_PEROID);
        }
    }

    private void countDistance() {
        Double currLat = Double.valueOf(lattitude);
        Double currLng = Double.valueOf(longitude);
        Double crossLat = stepslist.get(indexNextTurn).getStart_location().getLat();
        Double crossLng = stepslist.get(indexNextTurn).getStart_location().getLng();
        Log.d("x1", String.valueOf(currLat));
        Log.d("y1", String.valueOf(currLng));
        Log.d("x2", String.valueOf(crossLat));
        Log.d("y2", String.valueOf(crossLng));
        Log.d("pow", String.valueOf(Math.pow(4,5)));
        Double distance2point = Math.sqrt((Math.pow((crossLat-currLat),2)+Math.pow((crossLng-currLng),2)));

//        Toast.makeText(MapsActivity.this,
//                "ket : "+stepslist.get(indexNextTurn).getHtml_instructions()
//                        +"\ncurrlat  : "+currLat
//                        +"\ncurrlng  : "+currLng
//                        +"\ncrosslat : "+crossLat
//                        +"\ncrosslng : "+crossLat,
//                Toast.LENGTH_SHORT);

//        tvStatus.setText("ket : "+stepslist.get(indexNextTurn).getHtml_instructions()
//                +"\ncurrlat  : "+currLat
//                +"\ncurrlng  : "+currLng
//                +"\ncrosslat : "+crossLat
//                +"\ncrosslng : "+crossLng);

        Log.d("kirim",
                "ket : "+stepslist.get(indexNextTurn).getHtml_instructions()
                +"\ncurrlat  : "+currLat
                +"\ncurrlng  : "+currLng
                +"\ncrosslat : "+crossLat
                +"\ncrosslng : "+crossLng
        );

//        if (distance2point<=0.00045){
        if (distance2point<=0.000008*radius){
            indexNextTurn++;
        }
//        Log.d("distance2point", "countDistance: "+distance2point.toString());

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

//                Log.d("current location","Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude);
            }
            if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

//                Log.d("current location1","Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude);
            }
            if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

//                Log.d("current location2","Lattitude = " + lattitude
//                        + "\n" + "Longitude = " + longitude);
            }else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Language response
        String language = "language=id";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode+"&" +language;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.d("url",url);
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }

        return responseString;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            ArrayList<Steps> stepsArrayList = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
                stepslist = directionsParser.getStepslist();
                stepslist.size();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class ModelOutput {
        private String keterangan;
        private Double latSekarang;
        private Double lngSekarang;
        private Double latPersimpangan;
        private Double lngPersimpangan;

        public ModelOutput() {
        }

        public String getKeterangan() {
            return keterangan;
        }

        public void setKeterangan(String keterangan) {
            this.keterangan = keterangan;
        }

        public Double getLatSekarang() {
            return latSekarang;
        }

        public void setLatSekarang(Double latSekarang) {
            this.latSekarang = latSekarang;
        }

        public Double getLngSekarang() {
            return lngSekarang;
        }

        public void setLngSekarang(Double lngSekarang) {
            this.lngSekarang = lngSekarang;
        }

        public Double getLatPersimpangan() {
            return latPersimpangan;
        }

        public void setLatPersimpangan(Double latPersimpangan) {
            this.latPersimpangan = latPersimpangan;
        }

        public Double getLngPersimpangan() {
            return lngPersimpangan;
        }

        public void setLngPersimpangan(Double lngPersimpangan) {
            this.lngPersimpangan = lngPersimpangan;
        }
    }
}
