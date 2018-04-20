package com.markfeldman.theweatheroutside.services;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class FireBaseJobService extends JobService {
    private AsyncTask<Void,Void,Void> fetchMovieDate;

    @Override
    public boolean onStartJob(final JobParameters job) {
        fetchMovieDate = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeatherDB(context);
                jobFinished(job,false);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job,false);
            }
        };
        fetchMovieDate.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (fetchMovieDate!=null){
            fetchMovieDate.cancel(true);
        }

        return true;
    }
}