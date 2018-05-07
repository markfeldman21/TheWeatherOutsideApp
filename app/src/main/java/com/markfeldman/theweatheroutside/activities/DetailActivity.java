package com.markfeldman.theweatheroutside.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String weatherID;
    private final String WEATHER_ID_FOR_LOADER_KEY = "weather_id";
    private final int LOADER_ID = 33;
    private String weatherData;
    private TextView retrievedTemp;
    private TextView date;
    private TextView detailConditions;
    private ImageView weatherImage;
    private final String BUNDLE_EXTRA_INT_ID = "Bundle Extra";
    private String[] projection = {WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK, WeatherContract.WeatherData.COLUMN_DATE,
            WeatherContract.WeatherData.COLUMN_ICON, WeatherContract.WeatherData.COLUMN_CONDITIONS, WeatherContract.WeatherData.COLUMN_HUMIDITY,
            WeatherContract.WeatherData.COLUMN_HIGH_TEMPC, WeatherContract.WeatherData.COLUMN_HIGH_TEMPF};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        date = findViewById(R.id.date) ;//Starting with API 26, findViewById uses inference
        // for its return type, so you no longer have to cast.
        weatherImage = findViewById(R.id.detail_weather_image);
        detailConditions =findViewById(R.id.detail_conditions);
        retrievedTemp = findViewById(R.id.retrieved_temp);
        weatherID = getIntent().getStringExtra(BUNDLE_EXTRA_INT_ID);
        Toast.makeText(this,"ID = " + weatherID,Toast.LENGTH_LONG).show();
        startLoader(weatherID);
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




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("DETAIl", "CREATING LOADER IN DEATAIL! WEATHER ID ===== " + weatherID);
        String weatherIDRetrieved = args.getString(WEATHER_ID_FOR_LOADER_KEY);
        Uri weatherQueryUri = WeatherContract.WeatherData.CONTENT_URI;
        weatherQueryUri = weatherQueryUri.buildUpon().appendPath(weatherIDRetrieved).build();
        String selectionWhere = WeatherContract.WeatherData._ID + "=?";

        return new CursorLoader(this,weatherQueryUri,projection,selectionWhere,new String[]{weatherID},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0){
        }else if (data.getCount() == 0) {
            startLoader(weatherID);
        }

        String dayOfWeek = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK));
        String weatherDate = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_DATE));
        String humidity = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_HUMIDITY));
        String iconURL = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_ICON));
        String conditions =data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_CONDITIONS));
        String highCelcius = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPC));
        String highFah = data.getString(data.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPF));
        String finalUnit = "";
        String prefUnit = WeatherPreferences.getPreferredUnits(this);
        if (prefUnit.equals("metric")){
            finalUnit = highCelcius + " C";
        }else if (prefUnit.equals("imperial")) {
            finalUnit = highFah + " F";
        }
        Picasso.get().load(iconURL).into(weatherImage);
        weatherData = highCelcius + " " + highFah;
        retrievedTemp.setText(weatherData);
        date.setText(dayOfWeek + " " + weatherDate);
        detailConditions.setText(conditions);
        retrievedTemp.setText(finalUnit);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void startLoader(String weatherRowId){
        Bundle queryBundle = new Bundle();
        queryBundle.putString(WEATHER_ID_FOR_LOADER_KEY,weatherRowId);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(LOADER_ID);
        if (loader==null){
            loaderManager.initLoader(LOADER_ID,queryBundle,this);
        }else{
            loaderManager.restartLoader(LOADER_ID,queryBundle,this);
        }


    }

}
