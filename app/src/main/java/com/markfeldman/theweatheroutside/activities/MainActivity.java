package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity implements WeatherRecyclerViewAdapter.WeatherRowClicked {
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
        new RetrieveWeatherOnline().execute(defaultLocation);
    }

    private void errorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void displayWeather(){
        mRecyclerView.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            mRecyclerView.setVisibility(View.INVISIBLE);
            retrieveData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClicked(String weather) {
        Intent showDetailActivity = new Intent(MainActivity.this, DetailActivity.class);
        showDetailActivity.putExtra(Intent.EXTRA_TEXT,weather);
        startActivity(showDetailActivity);
    }

    public class RetrieveWeatherOnline extends AsyncTask<String, Void, String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... defWeather) {

            if (defWeather.length == 0) {
                return null;
            }

            String defaultWeather = defWeather[0];
            String okHttpResponse = null;
            String[] jsonResults = null;
            try {
                okHttpResponse = NetworkUtils.okHttpDataRetrieval(defaultWeather);
                jsonResults = JsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,okHttpResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonResults;
        }

        @Override
        protected void onPostExecute(String[] jsonResults) {
            progressBar.setVisibility(View.INVISIBLE);

            if (jsonResults!=null){
                displayWeather();
                weatherRecyclerViewAdapter.setWeatherData(jsonResults);
            }else{
                errorMessage();
            }
        }
    }
}
