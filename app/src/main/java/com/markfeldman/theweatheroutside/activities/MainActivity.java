package com.markfeldman.theweatheroutside.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.markfeldman.theweatheroutside.R;

public class MainActivity extends AppCompatActivity {
    private TextView weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherData = (TextView)findViewById(R.id.weather_data);

        String[] mockWeather = {"Monday - Cloudy - 72", "Tuesday - Sunny - 80", "Wednesday - Rainy - 90",
                "Monday - Cloudy - 72", "Tuesday - Sunny - 80", "Wednesday - Rainy - 90",
                "Monday - Cloudy - 72", "Tuesday - Sunny - 80", "Wednesday - Rainy - 90",
                "Monday - Cloudy - 72", "Tuesday - Sunny - 80", "Wednesday - Rainy - 90",
                "Monday - Cloudy - 72", "Tuesday - Sunny - 80", "Wednesday - Rainy - 90"};

        for (String mock : mockWeather){
            weatherData.append(mock + "\n\n");
        }
    }
}
