package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;
import com.markfeldman.theweatheroutside.utilities.WeatherRecyclerViewAdapter;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements WeatherRecyclerViewAdapter.WeatherRowClicked,
        LoaderManager.LoaderCallbacks<String[]>{
    private final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 22;
    private static final String WEATHER_LOCATION_FOR_LOADER = "WEATHER LOCATION";
    private TextView errorMessage;
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private WeatherRecyclerViewAdapter weatherRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorMessage = (TextView)findViewById(R.id.tv_error_message_display);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.weather_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(weatherRecyclerViewAdapter);
        retrieveData();
    }

    private void retrieveData(){
        String defaultLocation = WeatherPreferences.getPreferredWeatherLocation(this);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(WEATHER_LOCATION_FOR_LOADER,defaultLocation);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(LOADER_ID);
        if (loader==null){
            Log.d(TAG, "LOADER IS NULL");
            loaderManager.initLoader(LOADER_ID,queryBundle,this);
        }else{
            Log.d(TAG, "LOADER EXISTS RESTARTING");
            invalidateData();
            loaderManager.restartLoader(LOADER_ID,queryBundle,this);
        }


    }

    private void errorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void displayWeather(){
        mRecyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void invalidateData() {
        weatherRecyclerViewAdapter.setWeatherData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:{
                mRecyclerView.setVisibility(View.INVISIBLE);
                retrieveData();
                break;
            }
            case R.id.show_map:{
                showMap();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap(){
        String address ="32 washburn ave, newton, ma";
        String geoScheme = "geo:0,0?q=";

        Uri geoLocation = Uri.parse(geoScheme + address);
        Intent showMap = new Intent(Intent.ACTION_VIEW,geoLocation);

        if (showMap.resolveActivity(getPackageManager()) != null) {
            startActivity(showMap);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }

    }

    @Override
    public void onClicked(String weather) {
        Intent showDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        showDetailActivity.putExtra(Intent.EXTRA_TEXT,weather);
        startActivity(showDetailActivity);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        Log.d(TAG, "LOADER IS CREATED");
        return new AsyncTaskLoader<String[]>(this) {
            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    Log.d(TAG, "DATA IS NOT NULL!!!!!!");
                    deliverResult(mWeatherData);
                } else {
                    Log.d(TAG, "DATA IS NULL FORCE LOADER!!!!!!");
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                Log.d(TAG, "LOADER IN BACKGROUND!!!!!!");
                String weatherLocation = args.getString(WEATHER_LOCATION_FOR_LOADER);
                if (weatherLocation==null || weatherLocation.isEmpty()){
                    return null;
                }
                String okHttpResponse = null;
                String[] jsonResults = null;
                try {
                    okHttpResponse = NetworkUtils.okHttpDataRetrieval(weatherLocation);
                    jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,okHttpResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jsonResults;
            }

            @Override
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        Log.d(TAG, "LOADER IS DONE!!!!!");
        progressBar.setVisibility(View.INVISIBLE);

        if (data!=null){
            displayWeather();
            weatherRecyclerViewAdapter.setWeatherData(data);
        }else{
            errorMessage();
        }

    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
