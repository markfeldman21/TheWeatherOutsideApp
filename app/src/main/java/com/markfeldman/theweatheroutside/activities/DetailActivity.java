package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherContract;

public class DetailActivity extends AppCompatActivity {
    private String weatherID;
    private String weatherData;
    private TextView retrievedData;
    private final String BUNDLE_EXTRA_INT_ID = "Bundle Extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        retrievedData = (TextView)findViewById(R.id.retrieved_data);
        weatherID = getIntent().getStringExtra(BUNDLE_EXTRA_INT_ID);
        weatherData = retrieveWeatherFromDatabase(weatherID);

        retrievedData.setText(weatherData);

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
            case R.id.show_settings:{
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private String retrieveWeatherFromDatabase(String weatherID){
        String[] projection = {WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK, WeatherContract.WeatherData.COLUMN_DATE,
                WeatherContract.WeatherData.COLUMN_ICON_URL, WeatherContract.WeatherData.COLUMN_CONDITIONS, WeatherContract.WeatherData.COLUMN_HUMIDITY,
                WeatherContract.WeatherData.COLUMN_HIGH_TEMPC, WeatherContract.WeatherData.COLUMN_HIGH_TEMPF};
        Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
        weatherQueryUri = weatherQueryUri.buildUpon().appendPath(weatherID).build();
        String selectionWhere = WeatherContract.WeatherData._ID + "=?";
        Cursor mCursor = getContentResolver().query(weatherQueryUri,projection,selectionWhere,new String[]{weatherID},null);

        String dayOfWeek = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK));
        String weatherDate = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DATE));
        String humidity = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HUMIDITY));
        String iconURL = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_ICON_URL));
        String conditions = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_CONDITIONS));
        String highCelcius = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPC));
        String highFah = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPF));

        return dayOfWeek + " " + weatherDate + " " + humidity + " " + conditions + " " +  highCelcius + " " + highFah;
    }
}
