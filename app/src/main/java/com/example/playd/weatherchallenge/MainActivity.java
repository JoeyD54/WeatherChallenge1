package com.example.playd.weatherchallenge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_FINE_LOCATION = 1;
    TextView mCity, mTemperature, mErrorText;
    Button mTenDayBut, mGetCityTemp;
    String cityText, temperatureText, condUrl, city, state, stateCity;
    String[] splitStateCity;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTenDayBut = (Button) findViewById(R.id.tenDayBut);
        mGetCityTemp = (Button) findViewById(R.id.getCityTemp);
        mCity = (TextView) findViewById(R.id.cityName);
        mTemperature = (TextView) findViewById(R.id.temperature);
        mErrorText = (TextView) findViewById(R.id.ErrorText);
        requestQueue = Volley.newRequestQueue(this);


//Initial permission check
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            }
        }else{
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try{
                stateCity = getLocation(location.getLatitude(), location.getLongitude());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Not Found!", Toast.LENGTH_SHORT).show();
                mErrorText.setText("Location not found. Using default: Detroit, Michigan");
            }
        }

        if(stateCity != null) {
            splitStateCity = stateCity.split(" ");
            state = splitStateCity[0];
            city = splitStateCity[1];
        }
        else {
            city = "Detroit";
            state = "Michigan";
            stateCity = state + " " + city;
            Log.i(TAG, "Defaults have been set");

        }

        condUrl = "http://api.wunderground.com/api/90645b02d360fe14/conditions/q/" + state + "/" + city + ".json";

        mGetCityTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stateCity != null){
                    JsonCall(condUrl);
                }
                else{
                    Toast.makeText(MainActivity.this, "Trouble getting location. Please reset", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTenDayBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stateCity != null){
                    Intent i = new Intent(getApplicationContext(), TenDayForecast.class);
                    i.putExtra("key", stateCity);
                    startActivity(i);
                }
                else{
                    Toast.makeText(MainActivity.this, "Trouble getting location. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //This is here to request for gps permission again if the user declined.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case REQUEST_FINE_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            stateCity = getLocation(location.getLatitude(), location.getLongitude());
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Not Found!", Toast.LENGTH_SHORT).show();
                            mErrorText.setText("Location not found. Using default: Detroit, Michigan");
                        }
                    }
                }else{
                    Toast.makeText(this, "No Permission Granted!", Toast.LENGTH_SHORT).show();
                    mErrorText.setText("GPS not allowed. Using default: Detroit, Michigan");
                }
            }
        }
    }

    //Get nearest city
    public String getLocation(double lat, double lon) {
        List<Address> addressList;

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0) {
                city = addressList.get(0).getLocality();
                state = addressList.get(0).getAdminArea();
                stateCity = state + " " + city;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stateCity;
    }

    //Get city and temp of location and print it.
    public void JsonCall(String condUrl){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, condUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject obsvObj = response.getJSONObject("current_observation");
                    for(int i = 0; i < obsvObj.length(); i++){
                        JSONObject dispObj = obsvObj.getJSONObject("display_location");
                        for(int j = 0; j < dispObj.length(); j++){
                            cityText = dispObj.getString("city");
                        }
                        temperatureText = obsvObj.getString("temp_f");

                        mTemperature.setText(temperatureText + " F");
                        mCity.setText(cityText);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("Volley","Error");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}

