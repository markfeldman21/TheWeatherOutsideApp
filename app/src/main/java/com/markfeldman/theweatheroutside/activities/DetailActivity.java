package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.markfeldman.theweatheroutside.R;

public class DetailActivity extends AppCompatActivity {
    private String weatherData;
    private TextView retrievedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        retrievedData = (TextView)findViewById(R.id.retrieved_data);

        Intent checkForDataPassed = getIntent();
        if (checkForDataPassed!=null){
            if (checkForDataPassed.hasExtra(Intent.EXTRA_TEXT)){
                weatherData = checkForDataPassed.getStringExtra(Intent.EXTRA_TEXT);
                retrievedData.setText(weatherData);
            }

        }

    }
}
