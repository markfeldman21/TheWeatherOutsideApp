package com.markfeldman.theweatheroutside.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherDatabase;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {

    private static String TAG = JsonUtils.class.getSimpleName();
    private static ContentValues[] contentValuesArray;

    public static ContentValues[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {
        final String FORECAST_OBJECT = "forecast";
        final String SIMPLE_FORECAST = "simpleforecast";
        final String SIMPLE_FORECAST_ARRAY = "forecastday";
        final String DATE_OBJECT = "date";
        final String DAY_HIGH = "high";
        final String WEEKDAY = "weekday";
        final String DAY_NUMBER = "day";
        final String MONTH = "monthname";
        final String YEAR = "year";
        final String CELCIUS = "celsius";
        final String FAHREN = "fahrenheit";
        final String CONDITIONS = "conditions";
        final String HUMIDITY = "avehumidity";
        final String ICON = "icon";
        String selectedPrefUnits = WeatherPreferences.getPreferredUnits(context);

        JSONObject overallResponse = new JSONObject(forecastJsonStr);
        JSONObject forecast = overallResponse.getJSONObject(FORECAST_OBJECT);
        JSONObject simpleForecast = forecast.getJSONObject(SIMPLE_FORECAST);
        JSONArray simpleForecastArray = simpleForecast.getJSONArray(SIMPLE_FORECAST_ARRAY);

        String [] parsedWeather = new String [simpleForecastArray.length()];
        contentValuesArray = new ContentValues[simpleForecastArray.length()];

        for (int i = 0; i < simpleForecastArray.length(); i++){
            String day;
            String dayNumber;
            String month;
            String year;
            String finalDate;
            String humidity;
            String icon;
            String conditions;
            String highTempCelcius;
            String highTempF;
            String finalUnits = null;
            ContentValues cv = new ContentValues();

            JSONObject simpleForecastIndividual = simpleForecastArray.getJSONObject(i);
            JSONObject simpleForecastDate = simpleForecastIndividual.getJSONObject(DATE_OBJECT);
            JSONObject simpleForecasteHigh = simpleForecastIndividual.getJSONObject(DAY_HIGH);

            day = simpleForecastDate.getString(WEEKDAY);
            month = simpleForecastDate.getString(MONTH);
            year = simpleForecastDate.getString(YEAR);
            dayNumber = simpleForecastDate.getString(DAY_NUMBER);
            highTempCelcius = simpleForecasteHigh.getString(CELCIUS);
            highTempF = simpleForecasteHigh.getString(FAHREN);
            conditions = simpleForecastIndividual.getString(CONDITIONS);
            humidity = simpleForecastIndividual.getString(HUMIDITY);
            icon = simpleForecastIndividual.getString(ICON);
            finalDate = month + " " + dayNumber + " " + year;

            //MAYBE CHANGE DATABASE SCHEMA FOR ONE FINAL UNIT ENTRY INSTEAD OF BOTH
            if (selectedPrefUnits.equals("imperial")){
                finalUnits = highTempCelcius;
            }else if (selectedPrefUnits.equals("metric")){
                finalUnits = highTempF;
            }

            cv.put(WeatherContract.WeatherData.COLUMN_DATE, month + " " + dayNumber + " " + year);
            cv.put(WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK, day);
            cv.put(WeatherContract.WeatherData.COLUMN_HUMIDITY, humidity);
            cv.put(WeatherContract.WeatherData.COLUMN_ICON, icon);
            cv.put(WeatherContract.WeatherData.COLUMN_CONDITIONS,conditions);
            cv.put(WeatherContract.WeatherData.COLUMN_HIGH_TEMPC,highTempCelcius);
            cv.put(WeatherContract.WeatherData.COLUMN_HIGH_TEMPF, highTempF);

            contentValuesArray[i] = cv;
        }

        return contentValuesArray;

    }


    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}