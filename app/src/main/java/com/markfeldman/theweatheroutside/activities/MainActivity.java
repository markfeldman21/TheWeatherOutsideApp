package com.markfeldman.theweatheroutside.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.markfeldman.theweatheroutside.utilities.JsonUtils;
import com.markfeldman.theweatheroutside.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherData = (TextView)findViewById(R.id.weather_data);
        String defaultLocation = WeatherPreferences.getPreferredWeatherLocation(this);
        new RetrieveWeatherOnline().execute(defaultLocation);

    }

    public class RetrieveWeatherOnline extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... defWeather) {
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

            if (jsonResults!=null){
                for (String result : jsonResults){
                    weatherData.append(result + "\n\n\n");
                }
            }
        }
    }
}
