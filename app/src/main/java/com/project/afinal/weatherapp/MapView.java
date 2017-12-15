package com.project.afinal.weatherapp;


/**
 * SOFE4640 Final Project
 * Weather App
 *
 * @author Albert Fung, Alexei dela Pena, Daljit Sohi
 * Due: December 15, 2017
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapView extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=e243c640522e46b7a9e8936150c5c489";
    private String tileType = "";
    private String[] tileTypesArray;
    private TileOverlay tileOverlay;
    private Spinner spinner;
    private static String Location, CurrentTemp, Condition;
    private String weatherAPI_url;
    private LatLng newLatLng;
    private ImageView legendImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        spinner = findViewById(R.id.tile_options);
        tileTypesArray = new String[]{"Clouds", "Precipitation", "Pressure", "Wind", "Temperature"};

        legendImage = findViewById(R.id.legend);

        //handles the dropdown menu for the weather conditions overlay
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapView.this, android.R.layout.simple_spinner_dropdown_item, tileTypesArray);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        tileType = "clouds_new";
                        tileOverlay.remove();
                        Bitmap clouds = BitmapFactory.decodeResource(MapView.this.getResources(), R.raw.cloudbar);
                        legendImage.setImageBitmap(clouds);
                        initMap();
                        break;
                    case 1:
                        tileType = "precipitation_new";
                        tileOverlay.remove();
                        Bitmap snow = BitmapFactory.decodeResource(MapView.this.getResources(), R.raw.snowbar);
                        legendImage.setImageBitmap(snow);
                        initMap();
                        break;
                    case 2:
                        tileType = "pressure_new";
                        tileOverlay.remove();
                        Bitmap pressure = BitmapFactory.decodeResource(MapView.this.getResources(), R.raw.pressurebar);
                        legendImage.setImageBitmap(pressure);
                        initMap();
                        break;
                    case 3:
                        tileType = "wind_new";
                        tileOverlay.remove();
                        Bitmap wind = BitmapFactory.decodeResource(MapView.this.getResources(), R.raw.windbar);
                        legendImage.setImageBitmap(wind);
                        initMap();
                        break;
                    case 4:
                        tileType = "temp_new";
                        tileOverlay.remove();
                        Bitmap temp = BitmapFactory.decodeResource(MapView.this.getResources(), R.raw.temperaturebar);
                        legendImage.setImageBitmap(temp);
                        initMap();
                        break;
                }//end switch

            }//end onItemSelected()

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }//end onNothingSelected()
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_today:
                        Intent intent_today = new Intent(MapView.this, WeatherActivity.class);
                        startActivity(intent_today);
                        finish();
                        break;
                    case R.id.action_map:
                        break;
                    case R.id.action_Forecast:
                        Intent intent_forecast = new Intent(MapView.this, ForecastView.class);
                        startActivity(intent_forecast);
                        finish();
                        break;
                }
                return false;
            }
        });

//        //Initialize the Map
        initMap();

    }//end onCreate()

    //Initialize the Map
    private void initMap() {
        //Obtain support map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
    }//end initMap()

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapView.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 456);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //OverLay the Map with Weather Conditions
        final TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String format_url = String.format(OWM_TILE_URL, tileType, zoom, x, y);
                URL url = null;
                try {
                    url = new URL(format_url);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                return url;
            }
        };
        tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        //OnMapClick
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();

                newLatLng = latLng;
                weatherAPI_url = "http://api.wunderground.com/api/2b118671ed3cb3c2/conditions/q/" + newLatLng.latitude + "," + newLatLng.longitude + ".json";
                FetchWeatherData weatherFor_SelectedLocation = new FetchWeatherData();
                weatherFor_SelectedLocation.execute(weatherAPI_url);

                //Tile OverLay from Weather Imagery
                tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                        .tileProvider(tileProvider));
            }
        });//end onMapClickListener

    }//end onMapReady()

    /**
     * ------------------ AsynTask Class ----------------
     **/
    private class FetchWeatherData extends AsyncTask<String, Void, String> {
        String jsonData = null;
        JSONObject weatherData;
        StringBuilder sb = new StringBuilder();
        String location = "", currentTemp = "", conditionType = "";

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

            try {
                weatherData = new JSONObject(jsonData);
                JSONObject currentObservation_JSONObject = weatherData.getJSONObject("current_observation");
                JSONObject displayLocation_JSONObject = currentObservation_JSONObject.getJSONObject("display_location");

                location = displayLocation_JSONObject.getString("full");
                currentTemp = currentObservation_JSONObject.getString("temp_c");
                conditionType = currentObservation_JSONObject.getString("weather");

                Location = location;
                CurrentTemp = currentTemp;
                Condition = conditionType;

                //Set Marker Options
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(newLatLng.latitude, newLatLng.longitude))
                        .title(Location)
                        .snippet("Temperature: " + CurrentTemp + "\n" + "Current Conditions " + Condition + "\n")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(MapView.this);
                        info.setOrientation(LinearLayout.VERTICAL);
                        TextView title = new TextView(MapView.this);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(MapView.this);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());
                        info.addView(title);
                        info.addView(snippet);
                        return info;
                    }
                });
                mMap.addMarker(markerOptions).showInfoWindow(); //Set the Marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(newLatLng.latitude, newLatLng.longitude), 5f)); //Move the Camera to the Marker
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }//end onPostExecute()
    }//end FetchWeatherData.class
}//end MapView.class