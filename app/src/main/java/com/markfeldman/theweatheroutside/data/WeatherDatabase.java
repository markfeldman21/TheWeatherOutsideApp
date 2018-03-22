package com.markfeldman.theweatheroutside.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherDatabase {

    public static class WeatherDatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "weather.db";
        private static final int DATABASE_VERSION = 1;
        private final static String CREATE_DATABASE = "CREATE TABLE " + WeatherContract.WeatherData.TABLE_NAME +
                " ("+ WeatherContract.WeatherData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherContract.WeatherData.COLUMN_DATE + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HUMIDITY + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_ICON_URL  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_CONDITIONS  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HIGH_TEMPC  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HIGH_TEMPF  + " TEXT NOT NULL, " +
                ");";

        public WeatherDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherData.TABLE_NAME);
            onCreate(db);
        }
    }
}
