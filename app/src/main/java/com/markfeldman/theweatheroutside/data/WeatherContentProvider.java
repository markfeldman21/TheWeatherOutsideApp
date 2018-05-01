package com.markfeldman.theweatheroutside.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class WeatherContentProvider extends ContentProvider {
    private static final int WHOLE_WEATHER = 100;
    private static final int WEATHER_ID = 101;
    private WeatherDatabase weatherDatabase;
    private static UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher (){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_TASKS,WHOLE_WEATHER);
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY,WeatherContract.PATH_TASKS + "/#",WEATHER_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        weatherDatabase = new WeatherDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        weatherDatabase.openReadableDatabase();
        Cursor mCursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case WHOLE_WEATHER:{
                Log.d("CP", "IN ALL ROW CONTENT");
                mCursor = weatherDatabase.getAllRows();
                break;
            }
            case WEATHER_ID:{
                Log.d("CP", "IN SPECIFIC ROW CONTENT");
                mCursor = weatherDatabase.getSpecificRow(WeatherContract.WeatherData.TABLE_NAME,projection,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("UNKNOWN URI IN PROVIDER QUERY METHOD");

        }
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);


        return mCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        weatherDatabase.openWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numberOfRowsInserted =0;
        Log.d("CONTENT PROVIDER", "INSIDE BULK INSERT!!!" + " " + match + " " +uri);

        switch (match){
            case WHOLE_WEATHER: {

                weatherDatabase.beginTransaction();
                try{
                    for (ContentValues cv: values){
                        long id = weatherDatabase.insertRow(WeatherContract.WeatherData.TABLE_NAME,cv);
                        if (id!=-1){
                            numberOfRowsInserted++;
                        }else{
                            throw new android.database.SQLException("Failed To Insert Row");
                        }
                    }
                    weatherDatabase.transactionSuccesful();
                }finally {
                    weatherDatabase.endTransaction();
                    weatherDatabase.close();
                }
                if (numberOfRowsInserted>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        weatherDatabase.openWritableDatabase();
        weatherDatabase.deleteTable();
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String selectionStatement = WeatherContract.WeatherData._ID+"=?";
        return 0;
    }
}
