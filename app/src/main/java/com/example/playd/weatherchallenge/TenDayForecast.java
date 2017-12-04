package com.example.playd.weatherchallenge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joey on 12/1/2017.
 */

public class TenDayForecast extends AppCompatActivity {

    String fromMain, tenDayURL, city, state, day, period;
    String[] splitMainItems;
    TextView mDayLabel;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ten_day_forecast);

        mDayLabel = (TextView) findViewById(R.id.DayLabel);
        requestQueue = Volley.newRequestQueue(this);
        mDayLabel.setMovementMethod(new ScrollingMovementMethod());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fromMain = extras.getString("key");
        }

        splitMainItems = fromMain.split(" ");
        state = splitMainItems[0];
        city = splitMainItems[1];

        tenDayURL = "http://api.wunderground.com/api/90645b02d360fe14/forecast10day/q/" + state + "/" + city + ".json";

        JsonCall();

    }

    public void JsonCall() {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, tenDayURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject txtForecastObj = forecastObj.getJSONObject("txt_forecast");
                    JSONArray forecastArray = txtForecastObj.getJSONArray("forecastday");

                    for(int i = 0; i < forecastArray.length(); i++){
                        JSONObject actor = forecastArray.getJSONObject(i);
                        day = actor.getString("title");
                        period = actor.getString("period");

                        if(i % 2 == 0) {
                            mDayLabel.append(day + "\n\n");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
