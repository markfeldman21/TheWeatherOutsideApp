package com.markfeldman.theweatheroutside.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;

public class WeatherSyncTask {
    private static String TAG = WeatherSyncTask.class.getSimpleName();
    synchronized public static void syncWeatherDB(Context context){
        try{
            Log.d(TAG,"JOB WAS EXECUTED!!!!" );
            Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
            String weatherLocation = WeatherPreferences.getPreferredWeatherLocation(context);
            String okHttpResponse = NetworkUtils.okHttpDataRetrieval(weatherLocation);
            ContentValues[] jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(context,okHttpResponse);

            context.getContentResolver().delete(weatherQueryUri,null,null);
            context.getContentResolver().bulkInsert(weatherQueryUri,jsonResults);



            /*
            for (int i =0; i<jsonResults.length;i++){
                context.getContentResolver().update(weatherQueryUri,jsonResults[i],null,null);
            }
            */


        }catch (Exception e){

        }

    }
}
