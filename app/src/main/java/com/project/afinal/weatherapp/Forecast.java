package com.project.afinal.weatherapp;

/**
 * SOFE4640 Final Project
 * Weather App
 *
 * @author Albert Fung, Alexei dela Pena, Daljit Sohi
 * Due: December 15, 2017
 */

/**
 * This class stores info about each Forecast of each day
 * -> For each day this class stores
 *  - High Temp
 *  - Low Temp
 *  - Image Icon
 *  - Condition
 *  - Date
 */
public class Forecast {

    private String highTemp;
    private String lowTemp;
    private String condition;
    private String day;

    //Constructor
    public Forecast(String highTemp, String lowTemp, String condition, String day) {
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.condition = condition;
        this.day = day;
    }//end Constructor

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDate() {
        return day;
    }

    public void setDate(String date) {
        this.day = date;
    }
}//end Forecast.class
