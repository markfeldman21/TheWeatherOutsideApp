package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    public void shareMessage(){
        String mimeType = "text/plain";
        String shareTitle = "Share Weather";

        if (weatherData!=null){
            ShareCompat.IntentBuilder.from(this)
                    .setType(mimeType)
                    .setChooserTitle(shareTitle)
                    .setText(weatherData)
                    .startChooser();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.share_weather:{
                shareMessage();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
