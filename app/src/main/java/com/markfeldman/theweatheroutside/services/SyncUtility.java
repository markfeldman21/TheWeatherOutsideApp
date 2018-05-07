package com.markfeldman.theweatheroutside.services;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.markfeldman.theweatheroutside.data.WeatherContract;
import java.util.concurrent.TimeUnit;

public class SyncUtility {
    private static final int SYNC_INTERVAL_MINUTES = 0;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES);
    private static final int SYNC_NEXT = SYNC_INTERVAL_SECONDS;
    private static boolean initialized;
    private static final String WEATHER_SYNC_TAG = "weather_sync";

    synchronized public static void initialize(@NonNull final Context context){
        if (initialized){
            Log.d("SYNCUTILITY", "JOB IS INITIALIZED!");
            return;
        }
        initialized = true;

        scheduleFirebaseJobSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = WeatherContract.WeatherData.CONTENT_URI;
                String[] projection = {WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK, WeatherContract.WeatherData.COLUMN_DATE,
                        WeatherContract.WeatherData.COLUMN_ICON, WeatherContract.WeatherData.COLUMN_CONDITIONS, WeatherContract.WeatherData.COLUMN_HUMIDITY,
                        WeatherContract.WeatherData.COLUMN_HIGH_TEMPC, WeatherContract.WeatherData.COLUMN_HIGH_TEMPF};

                Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

                if (cursor == null|| cursor.getCount()==0){
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherIntentService.class);
        intentToSyncImmediately.setAction(WeatherIntentService.EXECUTE_NOW);
        context.startService(intentToSyncImmediately);
    }

    private static void scheduleFirebaseJobSync(@NonNull final Context context){

        try {
            Log.d("SYNCUTILITY", "JOB SCHEDULED!");
            com.firebase.jobdispatcher.Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);

            Job syncJob = firebaseJobDispatcher.newJobBuilder()
                    .setService(FireBaseJobService.class)
                    .setTag(WEATHER_SYNC_TAG)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(0,3))
                    .setReplaceCurrent(true)
                    .build();
            firebaseJobDispatcher.schedule(syncJob);

        }catch (Exception e){
            Log.v("TAG","PROBLEM IN SCHEDULING!!!!!!!!!!!!! + " + e);
        }
        Log.v("TAG","JOB TRIGGERED!!!!!!!!!!!!!");
    }

}
