package com.project.afinal.weatherapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This Activity Displays TODAY'S Weather -> The Weather is updated every 2.5 hours
 * Using "Open Weather Map API"
 */
public class WeatherActivity extends AppCompatActivity {

    private static final String LogT = "WEATHER_ACTIVITY :: ";
    //Declaring Variables

    DeviceLocation deviceLoc;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location myLocation;
    String wunderground_API;
    String weatherAPI_url;
    FetchWeatherData weatherData;
    TextView lbl_currentTemp, lbl_feelsLike, lbl_windSpeed, lbl_location, lbl_conditionsType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // --- TextView ---
        lbl_currentTemp = findViewById(R.id.CurrentTemp);
        lbl_feelsLike = findViewById(R.id.FeelsLike);
        lbl_windSpeed = findViewById(R.id.WindSpeed);
        lbl_location = findViewById(R.id.Location);
        lbl_conditionsType = findViewById(R.id.Condition_type);

        //Initializing Variables
        wunderground_API = getResources().getString(R.string.wunderground_API);
        weatherAPI_url = "http://api.wunderground.com/api/"+ wunderground_API+"/conditions/q/43.6532,-79.3832.json"; //Location -> Downtown Toronto
        weatherData = new FetchWeatherData(); //Download JSON data using the 'wunderground' API
        weatherData.execute(weatherAPI_url);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_today:
                        break;
                    case R.id.action_map:
                        Intent intent_map = new Intent(WeatherActivity.this, MapView.class);
                        startActivity(intent_map);
                        break;
                    case R.id.action_Forecast:
                        Intent intent_forecast = new Intent(WeatherActivity.this, ForecastView.class);
                        startActivity(intent_forecast);
                        break;
                }

                return false;
            }
        });

    }//end onCreate()

    //Button Click --> Fetch Toronto Data
    public void getWeatherData(View view){
        weatherData = new FetchWeatherData(); //Download JSON data using the 'wunderground' API
        weatherData.execute(weatherAPI_url);
    }//end getWeatherData()


    // ---------------------- Get Device Location ---------------------- //
    public void getDeviceLocation(){
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
                onLocationChange(location);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(LogT, "Error while getting Device Location");
            }
        });
    }//end getDeviceLocation()

    public void onLocationChange(Location loc){
        myLocation = loc;
        weatherAPI_url = "http://api.wunderground.com/api/" + wunderground_API + "/conditions/q/"+loc.getLatitude()+","+loc.getLongitude()+".json";
        weatherData = new FetchWeatherData(); //Download JSON data using the 'wunderground' API
        weatherData.execute(weatherAPI_url);
    }//end onLocationChange()


    /** ------------------ AsynTask Class ---------------- **/
    private class FetchWeatherData extends AsyncTask<String, Void, String> {
        String jsonData = null;
        JSONObject weatherData;
        StringBuilder sb = new StringBuilder();
        String currentTemp = "";
        String highTemp="", lowTemp="", feelsLike="", windSpeed="", location="", conditionType="";

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
                while((line = bufferedReader.readLine()) != null){
                    if(!line.trim().isEmpty()){
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
//            Log.d(LogT, " Fetching from URL = " + weatherAPI_url);
//            Log.d(LogT, "JSON DATA :: \n" + jsonData);

            try {
                weatherData = new JSONObject(jsonData);
                JSONObject currentObservation_JSONObject = weatherData.getJSONObject("current_observation");
                JSONObject displayLocation_JSONObject = currentObservation_JSONObject.getJSONObject("display_location");

                location = displayLocation_JSONObject.getString("full");
                currentTemp =  currentObservation_JSONObject.getString("temp_c");
                feelsLike = currentObservation_JSONObject.getString("feelslike_c");
                windSpeed = currentObservation_JSONObject.getString("wind_kph");
                conditionType = currentObservation_JSONObject.getString("weather");
                /* --> Verify if Property we are looking for is available in the JSON Object
                String high;
                if(currentObservation_JSONObject.has("high_c")){
                    high = currentObservation_JSONObject.getString("high_c");
                    Log.d(LogT, "Value of high: " + high);
                }else{
                    high = "---";
                    Log.d(LogT, "Value of high: " + high);
                }
                */
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DateFormat df = new SimpleDateFormat("EEE, d MMM");
            String currentDate = df.format(new Date());

            //Set TextViews
            lbl_currentTemp.setText(Math.round(Double.parseDouble(currentTemp)) + " °C"); //'°' --> ALT 248
            lbl_feelsLike.setText(feelsLike + " °C");
            lbl_windSpeed.setText(windSpeed + " K/H");
            lbl_location.setText(location);
            lbl_conditionsType.setText(conditionType);
        }//end onPostExecute()
    }//end FetchWeatherData.class

}//end WeatherActivity.class
