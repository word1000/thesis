package com.example.xiazhituo.ecase;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.amap.api.maps.CoordinateConverter.CoordType.GPS;

public class LocateMapActivity extends AppCompatActivity {

    MapView mMapView = null;
    String mAddressDescription = null;
    private AMap aMap;
    private GetGpsTask mGetGpsTask = null;
    private GetWifiLocationTask mGetWifiLocationTask = null;

    public EcaseApplication mEapp = null;
    public List<LatLng> mLatLngs = null;//new ArrayList<LatLng>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_map);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.locate_map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        mEapp = (EcaseApplication)getApplication();

        mLatLngs = new ArrayList<LatLng>();
        mLatLngs.clear();
        mGetGpsTask = new GetGpsTask();
        mGetGpsTask.execute();
        mGetWifiLocationTask = new GetWifiLocationTask();
        mGetWifiLocationTask.execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }


    public class GetGpsTask extends AsyncTask<Void, Void, Boolean> {

        GetGpsTask() {

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

                System.out.println("get gps : " + jsonObj.getString("gps"));

                JSONArray gpsJsonArray = new JSONArray(String.valueOf(jsonObj.getString("gps")));
                for (int i = 0; i < gpsJsonArray.length(); i++) {
                    JSONObject jo = (JSONObject)gpsJsonArray.get(i);
                    double lat = Double.valueOf(jo.getString("latitude")).doubleValue();
                    double lon = Double.valueOf(jo.getString("longtitude")).doubleValue();

                    CoordinateConverter converter = new CoordinateConverter(getBaseContext());
                    converter.from(CoordinateConverter.CoordType.GPS);

                    converter.coord(new LatLng(lat, lon));
                    LatLng location = converter.convert();
                    //LatLng location = new LatLng(lat, lon);

                    mLatLngs.add(location);

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
                aMap.addPolyline(
                        new PolylineOptions().addAll(mLatLngs).width(10).color(
                                Color.argb(255, 1, 1, 1)));


                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLngs.get(mLatLngs.size() - 1), 19));

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(mLatLngs.get(mLatLngs.size() - 1));
                markerOptions.title("当前位置");
                markerOptions.visible(true);
                markerOptions.snippet("DefaultMarker");
                aMap.addMarker(markerOptions);

                Toast.makeText(getBaseContext(), mAddressDescription, Toast.LENGTH_SHORT).show();

                //finish();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public class GetWifiLocationTask extends AsyncTask<Void, Void, Boolean> {

        GetWifiLocationTask() {

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

                String strUrlHaoService = "http://api.haoservice.com/api/getLocationByWifi?";
                String apiKey = "8438a9d6e8fc48989188c0be6919fa06";
                String reqType = "2";

                String apiGetRequestStr = strUrlHaoService + "mac1=" + mac1 + "&mac2=" + mac2
                        + "&type=" + reqType + "&key=" + apiKey;
                System.out.println(apiGetRequestStr);

                String wifiLocationStr = new EcaseGetHttpResponse().getHttpResponse(apiGetRequestStr);

                JSONObject jsonObj1 =  new JSONObject(String.valueOf(wifiLocationStr));
                JSONObject jsonObj2 = new JSONObject(String.valueOf(jsonObj1.getString("location")));

                mAddressDescription = String.valueOf(jsonObj2.getString("addressDescription"));

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
                Toast.makeText(getBaseContext(), mAddressDescription, Toast.LENGTH_SHORT).show();
            } else {

            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
