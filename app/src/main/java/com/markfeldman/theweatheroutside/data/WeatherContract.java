package com.markfeldman.theweatheroutside.data;

import android.provider.BaseColumns;

public class WeatherContract {
    private WeatherContract(){};

    //Inner Class For Each Seperate Table
    public static class WeatherData implements BaseColumns {
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
