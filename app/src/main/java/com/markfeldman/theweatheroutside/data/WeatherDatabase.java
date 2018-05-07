package com.markfeldman.theweatheroutside.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDatabase {
    private final String TAG = WeatherDatabase.class.getSimpleName();
    private Context context;
    private WeatherDatabaseHelper weatherDatabaseHelper;
    private SQLiteDatabase mDb;

    public WeatherDatabase(Context context){
        this.context = context;
        weatherDatabaseHelper = new WeatherDatabaseHelper(context);

    }

    public void openWritableDatabase(){
        mDb = weatherDatabaseHelper.getWritableDatabase();
    }

    public void openReadableDatabase(){
        mDb = weatherDatabaseHelper.getReadableDatabase();
    }

    public void close(){
        mDb.close();
    }

    public void beginTransaction(){
        mDb.beginTransaction();
    }

    public void transactionSuccesful(){
        mDb.setTransactionSuccessful();
    }

    public void endTransaction(){
        mDb.endTransaction();
    }

    public Cursor getAllRows(){
        return mDb.query(WeatherContract.WeatherData.TABLE_NAME,null,null,null,null,null,null);
    }

    public void updateRow(ContentValues weatherValue,String where, String selection){
            mDb.update(WeatherContract.WeatherData.TABLE_NAME,weatherValue,null,null);
    }

    public Cursor getSpecificRow(String tableName,String[] projection,String selection,String[] rowID){
        Cursor c = mDb.query(tableName,projection,selection,rowID,null,null,null);
        Log.d("CP", "IN DATABASE ==== "  + c.getCount());
        if (c!=null){
            c.moveToFirst();
        }
        return c;
    }

    public void deleteTable(){
        mDb.delete(WeatherContract.WeatherData.TABLE_NAME,null,null);
    }

    public void deleteAllRows(){
        Cursor c = getAllRows();
        int rowID = c.getColumnIndexOrThrow(WeatherContract.WeatherData._ID);
        String idToDelete = Integer.toString(rowID);
        String[]args = {idToDelete};
        if (c.moveToFirst()){
            do{
                mDb.delete(WeatherContract.WeatherData.TABLE_NAME," _id=?",args);
            }while(c.moveToNext());
            Log.d(TAG,"ALL ROWS DELETED");
        }else {
            Log.d(TAG,"NOTHING TO DELETE BEFORE INSERTION");
        }
    }

    //SQLite return statement returns long containing id of inserted Row
    public long insertRow(String table, ContentValues cv){
        return mDb.insert(table, null,cv);
    }

    public void insertAllRows(ContentValues[] values){
        int rowsInserted = 0;
        for (ContentValues value : values){
            long id = insertRow(WeatherContract.WeatherData.TABLE_NAME,value);
            if (id!=-1){
                rowsInserted++;
            }
        }
        Log.d("DATABASE", "INSIDE DATABASE INSERTED THIS MANY ROWS = " + rowsInserted);
    }


    public static class WeatherDatabaseHelper extends SQLiteOpenHelper{
        private static final String DATABASE_NAME = "weather.db";
        private static final int DATABASE_VERSION = 2;
        private final static String CREATE_DATABASE = "CREATE TABLE " + WeatherContract.WeatherData.TABLE_NAME +
                " ("+ WeatherContract.WeatherData._ID + " INTEGER PRIMARY KEY, " +
                WeatherContract.WeatherData.COLUMN_DATE + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HUMIDITY + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_ICON  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_CONDITIONS  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HIGH_TEMPC  + " TEXT NOT NULL, " +
                WeatherContract.WeatherData.COLUMN_HIGH_TEMPF  + " TEXT NOT NULL, " +
                " UNIQUE (" + WeatherContract.WeatherData.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        private WeatherDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherData.TABLE_NAME);
            onCreate(db);
        }
    }
}
