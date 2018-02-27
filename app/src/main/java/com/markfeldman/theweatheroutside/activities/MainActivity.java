package com.markfeldman.theweatheroutside.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView weatherData;
    private TextView errorMessage;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorMessage = (TextView)findViewById(R.id.tv_error_message_display);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        weatherData = (TextView)findViewById(R.id.weather_data);
    }

    private void retrieveData(){
        String defaultLocation = WeatherPreferences.getPreferredWeatherLocation(this);
        new RetrieveWeatherOnline().execute(defaultLocation);
    }

    private void errorMessage(){
        errorMessage.setVisibility(View.VISIBLE);
        weatherData.setVisibility(View.INVISIBLE);
    }

    private void displayWeather(){
        errorMessage.setVisibility(View.INVISIBLE);
        weatherData.setVisibility(View.VISIBLE);
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
            weatherData.setText("");
            retrieveData();
            weatherData.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
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
                for (String result : jsonResults){
                    weatherData.append(result + "\n\n\n");
                }
            }
        }
    }
}
