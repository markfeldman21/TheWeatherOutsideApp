package com.markfeldman.theweatheroutside.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class WeatherIntentService extends IntentService {
    public static final String DISMISS_ACTION_NOTIFICATION = "dimiss_notification";
    public static final String EXECUTE_NOW = "execute_now";


    public WeatherIntentService() {
        super("WeatherIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action!=null){
            if (action.equals(EXECUTE_NOW)){
                WeatherSyncTask.syncWeatherDB(this);
            }else if (action.equals(DISMISS_ACTION_NOTIFICATION)){
                //
            }
        }

    }
}
