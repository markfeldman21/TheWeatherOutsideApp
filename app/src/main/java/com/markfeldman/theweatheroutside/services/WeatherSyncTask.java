package com.markfeldman.theweatheroutside.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;

public class WeatherSyncTask {
    private static String TAG = WeatherSyncTask.class.getSimpleName();
    synchronized public static void syncWeatherDB(Context context){
        try{

            Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
            String weatherLocation = WeatherPreferences.getPreferredWeatherLocation(context);
            String okHttpResponse = NetworkUtils.okHttpDataRetrieval(weatherLocation);
            ContentValues[] jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(context,okHttpResponse);
            context.getContentResolver().delete(weatherQueryUri,null,null);
            context.getContentResolver().bulkInsert(weatherQueryUri,jsonResults);
            Log.d(TAG, "SYNCED DATABASE...." );
        }catch (Exception e){
            Log.d(TAG, "PROBLEM IN SYNC + " + e);
        }

    }
}
