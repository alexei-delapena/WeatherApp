package com.project.afinal.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * SOFE4640 Final Project
 * Weather App
 *
 * @author Albert Fung, Alexei dela Pena, Daljit Sohi
 *         Due: December 15, 2017
 */

public class ForecastListAdapter extends ArrayAdapter<Forecast> {

    private static final String TAG = "ForecastListAdapter";
    private Context mContext;
    private int mResource;

    public ForecastListAdapter(@NonNull Context context, int resource, ArrayList<Forecast> forecastList) {
        super(context, resource, forecastList);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get Forecast Info
        String highTemp = getItem(position).getHighTemp();
        String lowTemp = getItem(position).getLowTemp();
        String forecastCondition = getItem(position).getCondition();
        String forcastDay = getItem(position).getDate();

        //Create Forecast Object with the information
        Forecast forecast = new Forecast(highTemp, lowTemp, forecastCondition, forcastDay);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        //TextViews
        ImageView imageView = convertView.findViewById(R.id.weatherIcon);
        TextView hightemp = convertView.findViewById(R.id.lblHigh_Temp);
        TextView lowtemp = convertView.findViewById(R.id.lblLow_Temp);
        TextView condition = convertView.findViewById(R.id.lblCondition);
        TextView day = convertView.findViewById(R.id.lblDay);
        TextView units = convertView.findViewById(R.id.lblUnit);
        units.setText("C");

        hightemp.setText(highTemp + " °");
        lowtemp.setText(lowTemp + " °");
        condition.setText(forecastCondition);
        day.setText(forcastDay);

        //Snow Icon
        if (forecastCondition.equals("Light Snow")) {
            imageView.setImageResource(R.raw.snow);
        }
        if (forecastCondition.equals("Flurries")) {
            imageView.setImageResource(R.raw.snow);
        }
        if (forecastCondition.equals("Chance of Flurries")) {
            imageView.setImageResource(R.raw.snow);
        }
        if (forecastCondition.equals("Chance of Snow")) {
            imageView.setImageResource(R.raw.snow);
        }
        if (forecastCondition.equals("Snow")) {
            imageView.setImageResource(R.raw.snow);
        }
        if (forecastCondition.equals("Snow Showers")) {
            imageView.setImageResource(R.raw.snow);
        }

        //Cloud Icon
        if (forecastCondition.equals("Partly Cloudy")) {
            imageView.setImageResource(R.raw.cloudy);
        }
        if (forecastCondition.equals("Cloudy")) {
            imageView.setImageResource(R.raw.cloudy);
        }
        if (forecastCondition.equals("Fog")) {
            imageView.setImageResource(R.raw.cloudy);
        }
        if (forecastCondition.equals("Haze")) {
            imageView.setImageResource(R.raw.cloudy);
        }
        if (forecastCondition.equals("Mostly Cloudy")) {
            imageView.setImageResource(R.raw.cloudy);
        }
        //Rain Icon
        if (forecastCondition.equals("Chance of Sleet")) {
            imageView.setImageResource(R.raw.rain);
        }
        if (forecastCondition.equals("Chance of Rain")) {
            imageView.setImageResource(R.raw.rain);
        }
        if (forecastCondition.equals("Rain")) {
            imageView.setImageResource(R.raw.rain);
        }
        if (forecastCondition.equals("Sleet")) {
            imageView.setImageResource(R.raw.rain);
        }

        //Sunny Icon
        if (forecastCondition.equals("Clear")) {
            imageView.setImageResource(R.raw.clear);
        }
        if (forecastCondition.equals("Mostly Sunny")) {
            imageView.setImageResource(R.raw.clear);
        }
        if (forecastCondition.equals("Partly Sunny")) {
            imageView.setImageResource(R.raw.clear);
        }
        if (forecastCondition.equals("Sunny")) {
            imageView.setImageResource(R.raw.clear);
        }

        //Thunder Icon
        if (forecastCondition.equals("Chance of Storm")) {
            imageView.setImageResource(R.raw.thunder);
        }
        if (forecastCondition.equals("Storm")) {
            imageView.setImageResource(R.raw.thunder);
        }
        if (forecastCondition.equals("Thunder Storm")) {
            imageView.setImageResource(R.raw.thunder);
        }
        return convertView;
    }//end getView
}//end ForecastListAdapter.class
