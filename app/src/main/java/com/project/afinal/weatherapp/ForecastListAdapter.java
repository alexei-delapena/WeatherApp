package com.project.afinal.weatherapp;

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


public class ForecastListAdapter extends ArrayAdapter<Forecast> {

    private static final String TAG = "ForecastListAdapter";
    private Context mContext;
    private int mResource;

    public ForecastListAdapter(@NonNull Context context, int resource, ArrayList<Forecast> forecastList) {
        super(context, resource, forecastList);
        mContext = context;
        mResource = resource;
    }

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


        return convertView;
    }//end getView
}//end ForecastListAdapter.class
