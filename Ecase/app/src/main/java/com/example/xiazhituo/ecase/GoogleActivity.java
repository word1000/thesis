package com.example.xiazhituo.ecase;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GetGoogleGpsTask mGetGpsTask = null;
    private GetGoogleWifiLocationTask mGetWifiLocationTask = null;

    public EcaseApplication mEapp = null;
    public List<LatLng> mLatLngs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mEapp = (EcaseApplication)getApplication();

        mLatLngs = new ArrayList<LatLng>();
        mLatLngs.clear();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mGetGpsTask = new GetGoogleGpsTask();
        mGetGpsTask.execute();

        mGetWifiLocationTask  = new GetGoogleWifiLocationTask();
        mGetWifiLocationTask.execute();
    }

    public class GetGoogleGpsTask extends AsyncTask<Void, Void, Boolean> {

        GetGoogleGpsTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                String strUrlBase = "http://121.40.128.16/api/gps/";
                String strUserId = mEapp.getUserId();
                int gpsPointsNum = 100;
                String strUrl = strUrlBase + strUserId + "/" +gpsPointsNum;

                String responseString = new EcaseGetHttpResponse().getHttpResponse(strUrl);

                JSONObject jsonObj =  new JSONObject(String.valueOf(responseString));

                JSONArray gpsJsonArray = new JSONArray(String.valueOf(jsonObj.getString("gps")));
                for (int i = 0; i < gpsJsonArray.length(); i++) {
                    JSONObject jo = (JSONObject) gpsJsonArray.get(i);
                    double lat = Double.valueOf(jo.getString("latitude")).doubleValue();
                    double lon = Double.valueOf(jo.getString("longtitude")).doubleValue();

                    LatLng latLng = new LatLng(lat, lon);

                    mLatLngs.add(latLng);

                    System.out.println("latitude : " + jo.getString("latitude"));
                    System.out.println("longtitude : " + jo.getString("longtitude"));
                }
            } catch (JSONException je) {
                je.printStackTrace();
                System.out.println("json error:" + je.toString());
            }

            if (mLatLngs.isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {

                System.out.println("Add Poly Line");

                PolylineOptions polylineOptions = new PolylineOptions();
                for(int i = 0; i< mLatLngs.size(); i++){
                    polylineOptions.add(mLatLngs.get(i));
                }

                polylineOptions.geodesic(true).width(10).color(Color.argb(255, 1, 1, 1));

                mMap.addPolyline(polylineOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngs.get(mLatLngs.size() - 1), 19));
                mMap.addMarker(new MarkerOptions()
                                .title("E-Case")
                                .snippet("Your E-Case is here")
                                .position(mLatLngs.get(mLatLngs.size() - 1)));

                //finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetGoogleWifiLocationTask extends AsyncTask<Void, Void, Boolean> {

        GetGoogleWifiLocationTask() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                String wifiUrl = "http://121.40.128.16/api/wifi/";
                String deviceId = mEapp.deviceId;

                String wifiInfo = new EcaseGetHttpResponse().getHttpResponse(wifiUrl + deviceId);

                JSONObject jsonObj =  new JSONObject(String.valueOf(wifiInfo));
                String mac1 = jsonObj.getString("bssid1");
                String mac2 = jsonObj.getString("bssid2");

                String strUrlGoogleWifiService = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBDSQ_kgh-yLmclsZTC4g5LbpaSJ9PMYUo";


                String jsonStr = "{\"considerIp\": \"false\",\"wifiAccessPoints\": [{\"macAddress\": \"" + mac1  + "\",\"signalStrength\": -43,\"signalToNoiseRatio\": 0}, {\"macAddress\": \"" + mac2 + "\",\"signalStrength\": -55,\"signalToNoiseRatio\": 0} ]}";

                String wifiLocationStr = new EcaseGetHttpResponse().getPostHttpResponse(strUrlGoogleWifiService, jsonStr);

                JSONObject jsonObj1 =  new JSONObject(String.valueOf(wifiLocationStr));
                JSONObject jsonObj2 = new JSONObject(String.valueOf(jsonObj1.getString("location")));

                String wifiLat = String.valueOf(jsonObj2.getString("lat"));
                String wifiLng = String.valueOf(jsonObj2.getString("lng"));

            } catch (JSONException je) {
                je.printStackTrace();
                System.out.println("json error:" + je.toString());
            }

            if (mLatLngs.isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {

                System.out.println("Add Poly Line");

                PolylineOptions polylineOptions = new PolylineOptions();
                for(int i = 0; i< mLatLngs.size(); i++){
                    polylineOptions.add(mLatLngs.get(i));
                }

                polylineOptions.geodesic(true).width(10).color(Color.argb(255, 1, 1, 1));

                mMap.addPolyline(polylineOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngs.get(mLatLngs.size() - 1), 19));
                mMap.addMarker(new MarkerOptions()
                        .title("E-Case")
                        .snippet("Your E-Case is here")
                        .position(mLatLngs.get(mLatLngs.size() - 1)));

                //finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
