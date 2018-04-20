package com.markfeldman.theweatheroutside.services;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.markfeldman.theweatheroutside.activities.MainActivity;
import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;

public class WeatherSyncTask {
    synchronized public static void syncWeatherDB(Context context){
        try{
            Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
            String weatherLocation = WeatherPreferences.getPreferredUnits(context);
            String okHttpResponse = NetworkUtils.okHttpDataRetrieval(weatherLocation);
            ContentValues[] jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(context,okHttpResponse);
            context.getContentResolver().delete(weatherQueryUri,null,null);
            context.getContentResolver().bulkInsert(weatherQueryUri,jsonResults);

        }catch (Exception e){

        }

    }
}
