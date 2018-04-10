package com.markfeldman.theweatheroutside.activities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
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
import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherDatabase;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;
import com.markfeldman.theweatheroutside.utilities.WeatherRecyclerViewAdapter;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements WeatherRecyclerViewAdapter.WeatherRowClicked,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener{
    private final String TAG = MainActivity.class.getSimpleName();
    private final String BUNDLE_EXTRA_INT_ID = "Bundle Extra";
    private static final int LOADER_ID = 22;
    private WeatherDatabase weatherDatabase = new WeatherDatabase(this);
    private static final String WEATHER_LOCATION_FOR_LOADER = "WEATHER LOCATION";
    private TextView errorMessage;
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private WeatherRecyclerViewAdapter weatherRecyclerViewAdapter;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private String[] projection = {WeatherContract.WeatherData.COLUMN_CONDITIONS,WeatherContract.WeatherData.COLUMN_DATE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorMessage = findViewById(R.id.tv_error_message_display);
        progressBar = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.weather_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(weatherRecyclerViewAdapter);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        retrieveData();
    }

    @Override
    protected void onStart() {
        if (PREFERENCES_HAVE_BEEN_UPDATED){
            Log.d (TAG,"PREFERENCES CHANGED, LOADER RESET");
            retrieveData();
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
        super.onStart();
    }

    private void retrieveData(){
        String preferenceLocation = WeatherPreferences.getPreferredWeatherLocation(this);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(WEATHER_LOCATION_FOR_LOADER,preferenceLocation);
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
            case R.id.show_settings:{
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMap(){
        String address = WeatherPreferences.getPreferredWeatherLocation(this);
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
    public void onClicked(String weatherID) {
        Intent showDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        showDetailActivity.putExtra(BUNDLE_EXTRA_INT_ID,weatherID);
        startActivity(showDetailActivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        Log.d(TAG, "LOADER IS CREATED");
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mCursor = null;

            @Override
            protected void onStartLoading() {
                if (mCursor != null) {
                    deliverResult(mCursor);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @SuppressLint("NewApi")
            @Override
            public Cursor loadInBackground() {
                Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
                String weatherLocation = args.getString(WEATHER_LOCATION_FOR_LOADER);
                if (weatherLocation==null || weatherLocation.isEmpty()){
                    return null;
                }
                String okHttpResponse = null;
                ContentValues[] jsonResults = null;
                try {
                    Log.d(TAG, "LOADER IN BACKGROUND!!!!!!");
                    okHttpResponse = NetworkUtils.okHttpDataRetrieval(weatherLocation);
                    jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,okHttpResponse);
                    getContentResolver().delete(weatherQueryUri,null,null);
                    getContentResolver().bulkInsert(weatherQueryUri,jsonResults);

                    mCursor = getContentResolver().query(weatherQueryUri,null,null,null,null,null);


                    if (mCursor!=null){
                        mCursor.moveToFirst();
                    }else{
                        Log.d(TAG, "CURSOR IS NULL IN MAIN");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mCursor;
            }

            @Override
            public void deliverResult(Cursor data) {
                mCursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        if (key.equals(getString(R.string.location_key))){
            String setLocation = sharedPreferences.getString(getString(R.string.location_key), getString(R.string.preferences_default_value));
            Toast.makeText(this,"Location Changed!",Toast.LENGTH_LONG).show();
        }else if (key.equals(getString(R.string.pref_units_key))){
            Toast.makeText(this,"Unit Changed!",Toast.LENGTH_LONG).show();
        }
    }
}
