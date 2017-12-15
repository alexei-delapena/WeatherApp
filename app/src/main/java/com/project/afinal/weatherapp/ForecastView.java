package com.project.afinal.weatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * SOFE4640 Final Project
 * Weather App
 *
 * @author Albert Fung, Alexei dela Pena, Daljit Sohi
 *         Due: December 15, 2017
 */

public class ForecastView extends AppCompatActivity {

    private static final String LogT = "ForecastView";
    FusedLocationProviderClient fusedLocationProviderClient;
    FetchForecastData forecastData;
    ImageView imageView;
    TextView hightemp, lowtemp, condition, date, units;
    String wunderground_API;
    String weatherAPI_url;
    ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forcast);

        listView = findViewById(R.id.forecastListView);
        wunderground_API = getResources().getString(R.string.wunderground_API);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //Alert the User if GPS is Not Enabled
            AlertDialog.Builder builder = new AlertDialog.Builder(ForecastView.this);
            builder.setTitle("Please Enable GPS");
            builder.setMessage("Location Services must be enabled to receive weather data for your current location");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    weatherAPI_url = "http://api.wunderground.com/api/" + wunderground_API + "/conditions/q/43.6532,-79.3832.json"; //Location -> Downtown Toronto
                    forecastData = new FetchForecastData(); //Download JSON data using the 'wunderground' API
                    forecastData.execute(weatherAPI_url);
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }//end if block --> Check if location is enabled
        else {
            getDeviceLocation();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_today:
                        Intent intent_today = new Intent(ForecastView.this, WeatherActivity.class);
                        startActivity(intent_today);
                        finish();
                        break;
                    case R.id.action_map:
                        Intent intent_map = new Intent(ForecastView.this, MapView.class);
                        startActivity(intent_map);
                        finish();
                        break;
                    case R.id.action_Forecast:
                        break;
                }
                return false;
            }
        });

    }//end onCreate()

    // ---------------------- Get Device Location ---------------------- //
    public void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, 231);
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(LogT, "Location: " + location);
                onLocationChange(location);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LogT, "Error while getting Device Location");
            }
        });
    }//end getDeviceLocation()

    public void onLocationChange(Location loc) {
        Log.d(LogT, "Location: " + loc);
        weatherAPI_url = "http://api.wunderground.com/api/" + wunderground_API + "/forecast/q/" + loc.getLatitude() + "," + loc.getLongitude() + ".json";
        Log.d(LogT, "Fetching data from: " + weatherAPI_url);
        forecastData = new FetchForecastData();
        forecastData.execute(weatherAPI_url);
    }//end onLocationChange()

    /**
     * ------------------ AsyncTask Class ----------------
     **/
    private class FetchForecastData extends AsyncTask<String, Void, String> {
        String jsonData = null;
        JSONObject forecastData;
        StringBuilder sb = new StringBuilder();
        String hightemp = "", lowtemp = "", day = "", condition = "";
        ArrayList<Forecast> forcastList = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            /* Parsing JSON DATA
                -> JSON Data ULR ---->
            */
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        sb.append(line + "\n");
                    }
                }//end while loop
                jsonData = sb.toString();
                inputStream.close();
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonData;
        }//end doInBackground()

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Log.d("FORECAST DATA: ", jsonData);

            try {
                forecastData = new JSONObject(jsonData);
                JSONObject forcast_JSONObject = forecastData.getJSONObject("forecast");
                JSONObject simpleforecast_JSONObject = forcast_JSONObject.getJSONObject("simpleforecast");
                JSONArray forecastday_JSONArray = simpleforecast_JSONObject.getJSONArray("forecastday");

                // JSONObject inside an array
                /**     -------->           DAY_0             <--------       */
                JSONObject day_0_JSONObject = forecastday_JSONArray.getJSONObject(0);
                // -> Date
                JSONObject dateOf_day0_JSONObject = day_0_JSONObject.getJSONObject("date");
                day = dateOf_day0_JSONObject.getString("weekday") + ", " + dateOf_day0_JSONObject.getString("monthname_short") + " " + dateOf_day0_JSONObject.getString("day");
                Log.d("FORECAST DATA ::: ", "Date for Day at position 0 ===> " + day);
                // -> HighTemp
                JSONObject hightempOf_day0_JSONObject = day_0_JSONObject.getJSONObject("high");
                hightemp = hightempOf_day0_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "High Temperature for Day at position 0 ===> " + hightemp);
                // -> LowTemp
                JSONObject lowtempOf_day0_JSONObject = day_0_JSONObject.getJSONObject("low");
                lowtemp = lowtempOf_day0_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "Low Temperature for Day at position 0 ===> " + lowtemp);
                // -> Condition
                condition = day_0_JSONObject.getString("conditions");
                Log.d("FORECAST DATA ::: ", "Condition for Day at position 0 ===> " + condition);

                Forecast forecastDay0 = new Forecast(hightemp, lowtemp, condition, day);
                Log.d("FORECAST DATA ::: ", "Date of object forecastDay0 (Testing if it does work!)" + forecastDay0.getDate());
                forcastList.add(forecastDay0);


                /**     -------->           DAY_1             <--------       */
                JSONObject day_1_JSONObject = forecastday_JSONArray.getJSONObject(1);
                // -> Date
                JSONObject dateOf_day1_JSONObject = day_1_JSONObject.getJSONObject("date");
                day = dateOf_day1_JSONObject.getString("weekday") + ", " + dateOf_day1_JSONObject.getString("monthname_short") + " " + dateOf_day1_JSONObject.getString("day");
                Log.d("FORECAST DATA ::: ", "Date for Day at position 1 ===> " + day);
                // -> HighTemp
                JSONObject hightempOf_day1_JSONObject = day_1_JSONObject.getJSONObject("high");
                hightemp = hightempOf_day1_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "High Temperature for Day at position 1 ===> " + hightemp);
                // -> LowTemp
                JSONObject lowtempOf_day1_JSONObject = day_1_JSONObject.getJSONObject("low");
                lowtemp = lowtempOf_day1_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "Low Temperature for Day at position 1 ===> " + lowtemp);
                // -> Condition
                condition = day_1_JSONObject.getString("conditions");
                Log.d("FORECAST DATA ::: ", "Condition for Day at position 1 ===> " + condition);

                Forecast forecastDay1 = new Forecast(hightemp, lowtemp, condition, day);
                Log.d("FORECAST DATA ::: ", "Date of object forecastDay1 (Testing if it does work!)" + forecastDay1.getDate());
                forcastList.add(forecastDay1);


                /**     -------->           DAY_2             <--------       */
                JSONObject day_2_JSONObject = forecastday_JSONArray.getJSONObject(2);
                // -> Date
                JSONObject dateOf_day2_JSONObject = day_2_JSONObject.getJSONObject("date");
                day = dateOf_day2_JSONObject.getString("weekday") + ", " + dateOf_day2_JSONObject.getString("monthname_short") + " " + dateOf_day2_JSONObject.getString("day");
                Log.d("FORECAST DATA ::: ", "Date for Day at position 2 ===> " + day);
                // -> HighTemp
                JSONObject hightempOf_day2_JSONObject = day_2_JSONObject.getJSONObject("high");
                hightemp = hightempOf_day2_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "High Temperature for Day at position 2 ===> " + hightemp);
                // -> LowTemp
                JSONObject lowtempOf_day2_JSONObject = day_2_JSONObject.getJSONObject("low");
                lowtemp = lowtempOf_day2_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "Low Temperature for Day at position 2 ===> " + lowtemp);
                // -> Condition
                condition = day_2_JSONObject.getString("conditions");
                Log.d("FORECAST DATA ::: ", "Condition for Day at position 2 ===> " + condition);

                Forecast forecastDay2 = new Forecast(hightemp, lowtemp, condition, day);
                Log.d("FORECAST DATA ::: ", "Date of object forecastDay2 (Testing if it does work!)" + forecastDay2.getDate());
                forcastList.add(forecastDay2);


                /**     -------->           DAY_3             <--------       */
                JSONObject day_3_JSONObject = forecastday_JSONArray.getJSONObject(3);
                // -> Date
                JSONObject dateOf_day3_JSONObject = day_3_JSONObject.getJSONObject("date");
                day = dateOf_day3_JSONObject.getString("weekday") + ", " + dateOf_day3_JSONObject.getString("monthname_short") + " " + dateOf_day3_JSONObject.getString("day");
                Log.d("FORECAST DATA ::: ", "Date for Day at position 3 ===> " + day);
                // -> HighTemp
                JSONObject hightempOf_day3_JSONObject = day_3_JSONObject.getJSONObject("high");
                hightemp = hightempOf_day3_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "High Temperature for Day at position 3 ===> " + hightemp);
                // -> LowTemp
                JSONObject lowtempOf_day3_JSONObject = day_3_JSONObject.getJSONObject("low");
                lowtemp = lowtempOf_day3_JSONObject.getString("celsius");
                Log.d("FORECAST DATA ::: ", "Low Temperature for Day at position 3 ===> " + lowtemp);
                // -> Condition
                condition = day_3_JSONObject.getString("conditions");
                Log.d("FORECAST DATA ::: ", "Condition for Day at position 3 ===> " + condition);

                Forecast forecastDay3 = new Forecast(hightemp, lowtemp, condition, day);
                Log.d("FORECAST DATA ::: ", "Date of object forecastDay3 (Testing if it does work!)" + forecastDay3.getDate());
                forcastList.add(forecastDay3);


                ForecastListAdapter forecastListAdapter = new ForecastListAdapter(ForecastView.this, R.layout.forecast_list_layout, forcastList);
                listView.setAdapter(forecastListAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }//end onPostExecute()

    }//end FetchWeatherData.class
}//Five Day Forecast

