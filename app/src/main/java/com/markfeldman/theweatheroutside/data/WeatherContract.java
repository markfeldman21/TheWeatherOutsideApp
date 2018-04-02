package com.markfeldman.theweatheroutside.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

public class WeatherContract {
    public static final String AUTHORITY = "com.markfeldman.theweatheroutside";
    public  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "weather_table";
    private WeatherContract(){};


    //Inner Class For Each Seperate Table
    public static class WeatherData implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "weather_table";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DAY_OF_WEEK = "day";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_ICON_URL = "icon_url";
        public static final String COLUMN_CONDITIONS = "conditions";
        public static final String COLUMN_HIGH_TEMPC = "celcius";
        public static final String COLUMN_HIGH_TEMPF = "fahrenheit";

    }
}
